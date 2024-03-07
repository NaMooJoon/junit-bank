package shop.mtcoding.junitbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.transaction.Transaction;
import shop.mtcoding.junitbank.domain.transaction.TransactionRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountTransferReqDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountDepositResDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountSaveResDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountListResDto;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountDepositReqDto;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks
    private AccountService accountService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Spy
    private ObjectMapper om;

    @Test
    void 계좌등록_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        // stub
        User testUser = newMockUser(1L, "Joon", "JUNHYUN");
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.save(any())).thenReturn(testAccount);
        
        // when
        AccountSaveResDto accountSaveResDto = accountService.계좌등록(accountSaveReqDto, testUser.getId());
        String responseBody = om.writeValueAsString(accountSaveResDto);
        System.out.println("responseBody = " + responseBody);

        // then
        assertThat(accountSaveResDto.getId()).isEqualTo(1L);
        assertThat(accountSaveResDto.getNumber()).isEqualTo(1111L);
        assertThat(accountSaveResDto.getBalance()).isEqualTo(1000L);
    }

    @Test
    void 계좌목록보기_유저별_test() throws Exception {
        // given
        Long userId = 1L;

        // stub
        User testUser = newMockUser(userId, "Joon", "JUNHYUN");
        Account testAccount1 = newMockAccount(1L, 1111L, 1000L, testUser);
        Account testAccount2 = newMockAccount(2L, 2222L, 1000L, testUser);
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByUser_id(any())).thenReturn(List.of(testAccount1, testAccount2));

        // when
        AccountListResDto accountListResDto = accountService.계좌목록보기_유저별(userId);
        String responseBody = om.writeValueAsString(accountListResDto);
        System.out.println("responseBody = " + responseBody);

        // then
        assertThat(accountListResDto.getFullname()).isEqualTo("JUNHYUN");
        assertThat(accountListResDto.getAccountDtos().size()).isEqualTo(2);
        assertThat(accountListResDto.getAccountDtos().get(1).getNumber()).isEqualTo(2222L);
    }

    @Test
    void 계좌삭제_test() throws Exception {
        // given
        Long number = 1111L;
        Long userId = 1L;

        // stub
        User testUser = newMockUser(2L, "ssar", "쌀");
        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(testAccount));

        // when
        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));
    }

    @Test
    void 계좌입금_test() throws Exception {
        // given
        final Long amount = 100L;
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(amount);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01012341234");

        // stub 1
        User testUser = newMockUser(1L, "ssar", "쌀");
        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(testAccount));

        // stub 2 ( stub 이 진행될 때 마다, 연관된 객체는 새로 만들어 주기 => Mock 특성상 타이밍 때문에 꼬일 수 있다)
        Account testAccount2 = newMockAccount(1L, 1111L, 1000L, testUser);
        Transaction testTransaction = newMockDepositTransaction(1L, testAccount2, amount);
        when(transactionRepository.save(any())).thenReturn(testTransaction);

        // when
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        System.out.println("test amount : " + accountDepositResDto.getTransaction().getDepositAccountBalance());
        System.out.println("test amount : " + testAccount.getBalance());

        // then
        assertThat(testAccount.getBalance()).isEqualTo(1100L);
        assertThat(testTransaction.getDepositAccountBalance()).isEqualTo(1100L);
    }

    @Test
    void 계좌출금_test() throws Exception {
        // given
        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User ssar = newMockUser(1L, "ssar", "쌀");
        Account account = newMockAccount(1L, 1111L, 1000L, ssar);
        // when
        if (amount <= 0L) { // 0원 체크
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        account.checkOwner(userId);
        account.checkSamePassword(password);
        account.withdraw(amount);

        // then
        assertThat(account.getBalance()).isEqualTo(900L);
    }

    @Test
    void 계좌이체_test() throws Exception {
        // given
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");

        User cos = newMockUser(1L, "cos", "코스");
        User ssar = newMockUser(2L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 11111L, 1000L, cos);
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, ssar);

        // when
        if (accountTransferReqDto.getWithdrawNumber().longValue() ==
                accountTransferReqDto.getDepositNumber().longValue()) {
            throw new CustomApiException("입출금 계좌는 동일할 수 없습니다.");
        }

        if (accountTransferReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 출금 소유자 확인 (로그인 한 사람과 동일한지)
        withdrawAccount.checkOwner(1L);

        // 비밀번호 확인
        withdrawAccount.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        // 계좌 잔액 확인
        withdrawAccount.checkBalance(accountTransferReqDto.getAmount());

        // 출금하기
        withdrawAccount.withdraw(accountTransferReqDto.getAmount());
        depositAccount.deposit(accountTransferReqDto.getAmount());

        // then
        assertThat(withdrawAccount.getBalance()).isEqualTo(900L);
        assertThat(depositAccount.getBalance()).isEqualTo(1100L);
    }
}