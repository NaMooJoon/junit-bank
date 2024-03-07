package shop.mtcoding.junitbank.config.dummy;

import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.transaction.Transaction;
import shop.mtcoding.junitbank.domain.transaction.TransactionEnum;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserEnum;

public class DummyObject {
    protected User newUser(String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@naver.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@naver.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Transaction newTransaction(Account withdrawAccount, Account depositAccount, Long amount, String gubun,
                                         String sender, String receiver, String tel) {
        return Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .depositAccountBalance(depositAccount.getBalance())
                .withdrawAccountBalance(null).amount(amount)
                .gubun(TransactionEnum.DEPOSIT).sender(sender)
                .receiver(receiver)
                .tel(tel)
                .build();
    }

    protected Transaction newMockDepositTransaction(Long id, Account account, Long amount) {
        account.deposit(amount);
        return Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .depositAccount(account)
                .depositAccountBalance(account.getBalance())
                .amount(100L).gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01012345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
