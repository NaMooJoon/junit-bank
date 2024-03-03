package shop.mtcoding.junitbank.domain.account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(Long number); // TODO: refactoring (User LAZY => 이것만 제외, Because: 계좌 소유자 확인시에 쿼리가 두번 나가기 때문에 join fetch)

    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
