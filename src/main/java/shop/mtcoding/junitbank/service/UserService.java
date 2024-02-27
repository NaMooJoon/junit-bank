package shop.mtcoding.junitbank.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.domain.user.UserRepository;
import shop.mtcoding.junitbank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.junitbank.dto.user.UserResponseDto.JoinResponseDto;
import shop.mtcoding.junitbank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Service는 DTO를 요청받고, DTO로 응답한다.
    @Transactional
    public JoinResponseDto 회원가입(JoinRequestDto joinRequestDto) {
        // 1. 동일 유저 네임 존재 검사
        Optional<User> userOptional = userRepository.findByUsername(joinRequestDto.getUsername());
        if (userOptional.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다.");
        }

        // 2. 패스워드 인코딩 + 회원가입
        User userPersistence = userRepository.save(joinRequestDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinResponseDto(userPersistence);
    }

}
