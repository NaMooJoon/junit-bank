package shop.mtcoding.junitbank.config.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.user.UserRequestDto.LoginRequestDto;

@Transactional // 테스트가 하나 끝나면 rollback 이 된다.
@ActiveProfiles("test")
@AutoConfigureMockMvc // 얘를 명시해줘야, `private MockMvc mvc;`를 autowired 할 수 있음
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 가짜 환경으로 Spring 에 있는 component 들을 스캔 할 수 있음.
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.save(newUser("ssar", "쌀"));
    }

    @Test
    void successfulAuthentication_test() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("ssar");
        requestDto.setPassword("1234");

        String requestBody = om.writeValueAsString(requestDto);

        // when
        ResultActions resultActions = mvc.perform(
                post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        // String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // System.out.println("responseBody = " + responseBody);
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        // then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
    }

    @Test
    void unsuccessfulAuthentication_test() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("ssar");
        requestDto.setPassword("12345");
        String requestBody = om.writeValueAsString(requestDto);

        // when
        ResultActions resultActions = mvc.perform(
                post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);

        // then
        resultActions.andExpect(status().isUnauthorized());

    }
}