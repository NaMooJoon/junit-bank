package shop.mtcoding.junitbank.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;

@Getter
@NoArgsConstructor // 스프링이 User 객체 생성을 할 때 빈 생성자로 new를 하기 때문.
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 4)
    private Long number; // 계좌 번호
    @Column(nullable = false, length = 4)
    private Long password; // 계좌 비번
    @Column(nullable = false)
    private Long balance; // 잔액 (기본값: 1_000원)

    // 항상 ORM에서 fk의 주인은 Many Entity 쪽이다.
    @ManyToOne(fetch = FetchType.LAZY) // account.getUser().필드호출() 을 해야 User 정보를 조회함
    private User user; // user_id

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId) {
        if (this.user.getId() != userId) { // user.getId() 는 FK 이기 때문에 값을 가지고 있다, 조회를 해도 SELECT 쿼리가 날라가지 않는다 (LAZY)
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }
}
