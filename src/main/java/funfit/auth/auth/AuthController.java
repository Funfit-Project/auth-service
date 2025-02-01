package funfit.auth.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.auth.dto.JoinRequest;
import funfit.auth.auth.dto.JoinResponse;
import funfit.auth.auth.dto.JwtDto;
import funfit.auth.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody JoinRequest joinRequest) throws JsonProcessingException {
        JoinResponse joinResponse = authService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 회원가입 성공", joinResponse));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        JwtDto jwtDto = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 로그인 성공", jwtDto));
    }

    @PostMapping("/tokens/renew")
    public JwtDto renewTokens(HttpServletRequest request) {
        return authService.renewTokens(request);
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }
}
