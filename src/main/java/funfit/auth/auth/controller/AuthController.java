package funfit.auth.auth.controller;

import funfit.auth.auth.dto.*;
import funfit.auth.utils.JwtUtils;
import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/auth/join")
    public ResponseEntity join(@RequestBody JoinRequest joinRequest) {
        JoinResponse joinResponse = authService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("사용자 회원가입 성공", joinResponse));
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        JwtDto jwtDto = jwtUtils.generateJwt(loginResponse.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 로그인 성공", jwtDto));
    }
}
