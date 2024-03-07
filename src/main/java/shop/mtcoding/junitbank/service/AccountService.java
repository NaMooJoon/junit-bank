package shop.mtcoding.junitbank.service;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.transaction.Transaction;
import shop.mtcoding.junitbank.domain.transaction.TransactionEnum;
import shop.mtcoding.junitbank.domain.transaction.TransactionRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountDepositResDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountListResDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountSaveResDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountWithdrawResDto;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public AccountSaveResDto 계좌등록(AccountSaveReqDto accountSaveReqDto, Long userId) {

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다"));

        Optional<Account> accountOptional = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if (accountOptional.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");
        }

        Account accountEntity = accountRepository.save(accountSaveReqDto.toEntity(userEntity));
        return new AccountSaveResDto(accountEntity);
    }

    public AccountListResDto 계좌목록보기_유저별(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));
        // 유저의 모든 계좌목록
        List<Account> accountEntityList = accountRepository.findByUser_id(userId);
        return new AccountListResDto(userEntity, accountEntityList);
    }

    @Transactional
    public void 계좌삭제(Long number, Long userId) {
        Account accountEntity = accountRepository.findByNumber(number)
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));
        accountEntity.checkOwner(userId);
        accountRepository.deleteById(accountEntity.getId());
    }

    @Transactional
    public AccountDepositResDto 계좌입금(AccountDepositReqDto accountDepositReqDto) {
        // 0원 체크
        if (accountDepositReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));
        // 입금 (해당 계좌 balance 조정)
        depositAccountPS.deposit(accountDepositReqDto.getAmount());
        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .depositAccount(depositAccountPS)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber() + "")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);
        return new AccountDepositResDto(depositAccountPS, transactionPS);
    }

    @Transactional
    public AccountWithdrawResDto 계좌출금(AccountWithdrawReqDto accountWithdrawReqDto, Long userId) {
        // 0원 체크
        if (accountWithdrawReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        // 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        // 출금 소유자 확인 (로그인 한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());

        // 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        // 거래 내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccount(null)
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber() + "")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO
        return new AccountWithdrawResDto(withdrawAccountPS, transactionPS);
    }



    @Getter
    @Setter
    public static class AccountWithdrawReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(WITHDRAW)$")
        private String gubun;
    }
}
