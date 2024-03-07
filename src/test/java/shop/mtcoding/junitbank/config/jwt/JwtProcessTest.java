package shop.mtcoding.junitbank.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import shop.mtcoding.junitbank.config.auth.LoginUser;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserEnum;

class JwtProcessTest {

    @Test
    void create_test() {
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);

        // when
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("test : " + jwtToken);

        // then
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    void verify_test() {
        // given
        String jwtToken = createToken();
        String token = jwtToken.replace(JwtVO.TOKEN_PREFIX, "");

        // when
        LoginUser loginUser2 = JwtProcess.verify(token);

        // then
        assertThat(loginUser2.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser2.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }

    private String createToken() {
        User user = User.builder()
                .id(1L)
                .role(UserEnum.ADMIN)
                .build();
        LoginUser loginUser = new LoginUser(user);

        // when
        return JwtProcess.create(loginUser);
    }
}