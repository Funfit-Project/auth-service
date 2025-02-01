package funfit.auth.user.controller;

import funfit.auth.user.dto.*;
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
    private final UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity readUserInfo(HttpServletRequest request) {
        ReadUserResponse readUserResponse = userService.readUserInfo(jwtUtils.getEmailFromHeader(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 조회 성공", readUserResponse));
    }

    @PutMapping("/edit")
    public ResponseEntity editUserInfo(@RequestBody EditUserInfoRequest editUserInfoRequest, HttpServletRequest request) throws InterruptedException {
        ReadUserResponse readUserResponse = userService.editUserInfo(editUserInfoRequest, jwtUtils.getEmailFromHeader(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 수정 성공", readUserResponse));
    }
}
