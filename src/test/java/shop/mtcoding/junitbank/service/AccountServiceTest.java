package shop.mtcoding.junitbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.account.AccountRepository;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountSaveResDto;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks
    private AccountService accountService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Spy
    private ObjectMapper om;

    @Test
    void 계좌등록_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        // stub
        User testUser = newMockUser(1L, "Joon", "JUNHYUN");
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.save(any())).thenReturn(testAccount);
        
        // when
        AccountSaveResDto accountSaveResDto = accountService.계좌등록(accountSaveReqDto, testUser.getId());
        String responseBody = om.writeValueAsString(accountSaveResDto);
        System.out.println("responseBody = " + responseBody);

        // then
        assertThat(accountSaveResDto.getId()).isEqualTo(1L);
        assertThat(accountSaveResDto.getNumber()).isEqualTo(1111L);
        assertThat(accountSaveResDto.getBalance()).isEqualTo(1000L);
    }
}