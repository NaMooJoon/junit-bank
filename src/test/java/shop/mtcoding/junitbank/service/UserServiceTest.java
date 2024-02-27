package shop.mtcoding.junitbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.junitbank.config.dummy.DummyObject;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserEnum;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.junitbank.dto.user.UserResponseDto.JoinResponseDto;

/**
 * 가짜 환경에서는
 * Spring 관련 Bean들이 하나도 없는 환경!
 * 따라서, `private UserService userService;`을 사용하고 싶다면, 연관된 Bean들을 등록해줘야 한다.
 * @Mock : 가짜로 메모리 상에 객체를 생성
 * @Spy : 얘는 진짜 객체를 들고 오는 것
 * @InjectMocks: @Mock과 @Spy로 만들어진 객체들을 주입해 줌.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * stub를 쓰는 이유
     * 실제 데이터베이스나 외부 서비스에 의존하지 않고도 테스트를 실행할 수 있도록 하는 것이 stubbing 이다.
     * 외부 서비스 경우 에러가 없다고 가정하고, 특정 결과물을 얻는다고 가정할 수 있다.
     * Stubbing을 통해 특정 조건에서 어떤 결과가 나와야 하는지를 명시적으로 정의하면, 같은 입력에 대해 항상 같은 결과를 얻을 수 있다.
     */
    @Test
    void 회원가입_test() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("Joon");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("Joon@naver.com");
        joinRequestDto.setFullname("JunHyun");

        // stub 1
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // stub 2
        User testUser = newMockUser(1L, "Joon", "JunHyun");
        when(userRepository.save(any())).thenReturn(testUser);

        // when
        JoinResponseDto joinResponseDto = userService.회원가입(joinRequestDto);
        System.out.println("테스트: " + joinResponseDto);

        // then
        assertThat(joinResponseDto.getId()).isEqualTo(1L);
        assertThat(joinResponseDto.getUsername()).isEqualTo("Joon");
    }
}