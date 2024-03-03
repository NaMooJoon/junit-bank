package shop.mtcoding.junitbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import shop.mtcoding.junitbank.dto.account.AccountResDto.AccountListResDto;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;

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

    @Test
    void 계좌목록보기_유저별_test() throws Exception {
        // given
        Long userId = 1L;

        // stub
        User testUser = newMockUser(userId, "Joon", "JUNHYUN");
        Account testAccount1 = newMockAccount(1L, 1111L, 1000L, testUser);
        Account testAccount2 = newMockAccount(2L, 2222L, 1000L, testUser);
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByUser_id(any())).thenReturn(List.of(testAccount1, testAccount2));

        // when
        AccountListResDto accountListResDto = accountService.계좌목록보기_유저별(userId);
        String responseBody = om.writeValueAsString(accountListResDto);
        System.out.println("responseBody = " + responseBody);

        // then
        assertThat(accountListResDto.getFullname()).isEqualTo("JUNHYUN");
        assertThat(accountListResDto.getAccountDtos().size()).isEqualTo(2);
        assertThat(accountListResDto.getAccountDtos().get(1).getNumber()).isEqualTo(2222L);
    }

    @Test
    void 계좌삭제_test() throws Exception {
        // given
        Long number = 1111L;
        Long userId = 1L;

        // stub
        User testUser = newMockUser(2L, "ssar", "쌀");
        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(testAccount));

        // when
        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));
    }
}