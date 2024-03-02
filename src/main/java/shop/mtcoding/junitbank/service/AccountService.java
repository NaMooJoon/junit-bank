package shop.mtcoding.junitbank.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountSaveResDto;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

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

}
