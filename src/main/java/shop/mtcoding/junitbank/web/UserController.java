package shop.mtcoding.junitbank.web;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.junitbank.dto.ResponseDto;
import shop.mtcoding.junitbank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.junitbank.dto.user.UserResponseDto.JoinResponseDto;
import shop.mtcoding.junitbank.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinRequestDto joinRequestDto, BindingResult bindingResult) {


        JoinResponseDto joinResDto = userService.회원가입(joinRequestDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinResDto), HttpStatus.CREATED);
    }
}
