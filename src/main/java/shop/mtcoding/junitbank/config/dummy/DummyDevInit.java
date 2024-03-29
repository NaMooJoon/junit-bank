package shop.mtcoding.junitbank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev") // dev 모드일 때만 실행 가능
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) {
        return (args -> {
            // 서버 실행시 무조건 실행
            User ssar = userRepository.save(newUser("ssar", "쌀"));
            User cos = userRepository.save(newUser("cos", "코스"));
            Account account1 = accountRepository.save(newAccount(1111L, ssar));
            Account account2 = accountRepository.save(newAccount(2222L, cos));
        });
    }
}
