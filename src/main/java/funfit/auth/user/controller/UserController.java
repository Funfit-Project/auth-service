package funfit.auth.user.controller;

import funfit.auth.kafka.KafkaProducerService;
import funfit.auth.user.dto.*;
import funfit.auth.user.service.JoinService;
import funfit.auth.utils.JwtUtils;
import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtils jwtUtils;
    private final JoinService joinService;
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody JoinRequest joinRequest) {
        JoinResponse joinResponse = joinService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 회원가입 성공", joinResponse));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = joinService.login(loginRequest);
        JwtDto jwtDto = jwtUtils.generateJwt(loginResponse.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("사용자 로그인 성공", jwtDto));
    }

    @GetMapping("/mypage")
    public ResponseEntity readUserInfo(HttpServletRequest request) {
        ReadUserResponse readUserResponse = userService.readUserInfo(jwtUtils.getEmailFromHeader(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 조회 성공", readUserResponse));
    }

    @PutMapping("/edit")
    public ResponseEntity editUserInfo(@RequestBody EditUserInfoRequest dto, HttpServletRequest request) {
        ReadUserResponse readUserResponse = userService.editUserInfo(dto, jwtUtils.getEmailFromHeader(request));
        kafkaProducerService.publishUserInfoUpdated(readUserResponse.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 수정 성공", readUserResponse));
    }
}
