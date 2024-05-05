package funfit.auth.mypage.controller;

import funfit.auth.responseDto.SuccessResponse;
import funfit.auth.mypage.dto.EditUserInfoRequest;
import funfit.auth.mypage.dto.ReadUserResponse;
import funfit.auth.user.service.UserService;
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
public class MyPageController {

    private final UserService userService;

    @GetMapping("/auth/mypage")
    public ResponseEntity readUserInfo(HttpServletRequest request) {
        ReadUserResponse readUserResponse = userService.readUserInfo(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 조회 성공", readUserResponse));
    }

    @PutMapping("/auth/edit")
    public ResponseEntity editUserInfo(@RequestBody EditUserInfoRequest dto, HttpServletRequest request) {
        ReadUserResponse readUserResponse = userService.editUserInfo(dto, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResponse("회원 정보 수정 성공", readUserResponse));
    }
}
