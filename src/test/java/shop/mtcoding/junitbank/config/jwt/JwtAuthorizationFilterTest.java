package shop.mtcoding.junitbank.config.jwt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.junitbank.config.auth.LoginUser;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc // 얘를 명시해줘야, `private MockMvc mvc;`를 autowired 할 수 있음
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 가짜 환경으로 Spring 에 있는 component 들을 스캔 할 수 있음.
class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void authorization_success_test() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test").header(JwtVO.HEADER, jwtToken));

        // then
        resultActions.andExpect(status().isNotFound()); // 404
    }

    @Test
    void authorization_fail_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));

        // then
        resultActions.andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void authorization_admin_test() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        // when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test").header(JwtVO.HEADER, jwtToken));

        // then
        resultActions.andExpect(status().isForbidden()); // 403
    }
}