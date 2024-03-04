package shop.mtcoding.junitbank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.user.UserRequestDto.JoinRequestDto;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        dataSetting();
    }

    @Test
    void join_success_test() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("love");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("love@naver.com");
        joinRequestDto.setFullname("러브");

        String requestBody = om.writeValueAsString(joinRequestDto);
        // System.out.println("테스트: requestBody > " + requestBody);
        // when
        ResultActions resultActions = mvc.perform(
                post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // System.out.println("테스트: responseBody > " + responseBody);
        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void join_fail_test() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("ssar");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("ssar@naver.com");
        joinRequestDto.setFullname("");

        String requestBody = om.writeValueAsString(joinRequestDto);
        // System.out.println("테스트: requestBody > " + requestBody);
        // when
        ResultActions resultActions = mvc.perform(
                post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // System.out.println("테스트: responseBody > " + responseBody);
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private void dataSetting() {
        userRepository.save(newUser("ssar", "쌀"));
        em.clear();
    }
}