package funfit.auth.user.controller;

import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.user.dto.JoinRequest;
import funfit.auth.user.dto.JoinResponse;
import funfit.auth.user.dto.LoginResponse;
import funfit.auth.user.dto.LoginRequest;
import funfit.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/auth/join")
    public ResponseEntity join(@RequestBody JoinRequest joinRequest) {
        JoinResponse joinResponse = userService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("사용자 회원가입 성공", joinResponse));
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 로그인 성공", loginResponse));
    }
}
