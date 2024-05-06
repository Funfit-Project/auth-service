package funfit.auth.userInfo.controller;

import funfit.auth.utils.JwtUtils;
import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.userInfo.dto.EditUserInfoRequest;
import funfit.auth.userInfo.dto.ReadUserResponse;
import funfit.auth.userInfo.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final JwtUtils jwtUtils;
    private final UserInfoService userInfoService;

    @GetMapping("/auth/mypage")
    public ResponseEntity readUserInfo(HttpServletRequest request) {
        ReadUserResponse readUserResponse = userInfoService.readUserInfo(jwtUtils.getEmailFromHeader(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 조회 성공", readUserResponse));
    }

    @PutMapping("/auth/edit")
    public ResponseEntity editUserInfo(@RequestBody EditUserInfoRequest dto, HttpServletRequest request) {
        ReadUserResponse readUserResponse = userInfoService.editUserInfo(dto, jwtUtils.getEmailFromHeader(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 수정 성공", readUserResponse));
    }
}
