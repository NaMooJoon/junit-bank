package shop.mtcoding.junitbank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountSaveReqDto;

@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        dataSetting();
    }

    /**
     * [사용자 인증 test 처리하는 방법]
     *      - jwt token -> 인증필터 -> 시큐리티 세션 생성이 원래 순서,
     *      - jwt token이 헤더에 없더라도 일단 인증필터는 통과하게 된다. 하지만, controller를 진입할 때에 권한 인증에 막하셔 exception이 터지는 것.
     *              - 즉, jwt token이 없더라도 controller 진입시 권한을 가진 세션을 가지고 있다면, controller에 접근할 수 있다는 의미이다.
     *      - @WithUserDetails(value = "ssar") => "ssar"이라는 이름을 가진 유저를 세션으로 이용하라는 의미.
     *              -> DB에서 username=ssar 조회를 통해 세션에 담아주는 어노테이션
     *
     * [DB에 테스트 user 미리 등록할 때 주의 사항]
     *      - setupBefore를 언제 수행할 것이냐
     *              - setupBefore=TEST_METHOD (setUp() 메서드 실행전에 수행)
     *              - setupBefore = TestExecutionEvent.TEST_EXECUTION (saveAccount_test() 메서드 실행전에 수행)
      */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void saveAccount_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);

        String requestBody = om.writeValueAsString(accountSaveReqDto);
        // when
        ResultActions resultActions = mvc.perform(
                post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void findUserAccount_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/account/login-user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    private void dataSetting() {
        User user = userRepository.save(newUser("ssar", "쌀"));
        Account account1 = accountRepository.save(newAccount(1111L, user));
        Account account2 = accountRepository.save(newAccount(2222L, user));
    }
}