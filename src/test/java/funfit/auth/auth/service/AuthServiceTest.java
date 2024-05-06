package funfit.auth.auth.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.auth.dto.JoinRequest;
import funfit.auth.auth.dto.JoinResponse;
import funfit.auth.auth.dto.LoginRequest;
import funfit.auth.auth.dto.LoginResponse;
import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    public void joinSuccess() {
        // given
        JoinRequest joinRequest = new JoinRequest("user@naver.com", "1234", "user", "회원", "01012345678");

        // when
        JoinResponse joinResponse = authService.join(joinRequest);

        // then
        Assertions.assertThat(joinResponse.getEmail()).isEqualTo("user@naver.com");
        Assertions.assertThat(joinResponse.getName()).isEqualTo("user");
        Assertions.assertThat(joinResponse.getRole()).isEqualTo("회원");
    }

    @Test
    @DisplayName("회원가입 실패-이메일 중복")
    public void joinFailByDuplicatedEmail() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));

        // when
        JoinRequest duplicatedEmailJoinRequest = new JoinRequest("user@naver.com", "1234", "user", "회원", "01012345678");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.join(duplicatedEmailJoinRequest);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    @DisplayName("로그인 성공")
    public void loginSuccess() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));

        // when
        LoginRequest loginRequest = new LoginRequest("user@naver.com", "1234");
        LoginResponse loginResponse = authService.login(loginRequest);

        // then
        Assertions.assertThat(loginResponse).isNotNull();
        Assertions.assertThat(loginResponse.getEmail()).isEqualTo("user@naver.com");
    }

    @Test
    @DisplayName("로그인 실패-이메일 오류")
    public void loginFailByEmail() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));
        LoginRequest loginRequest = new LoginRequest("userr@naver.com", "1234");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_EMAIL);
    }

    @Test
    @DisplayName("로그인 실패-패스워드 오류")
    public void loginFailByPassword() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));
        LoginRequest loginRequest = new LoginRequest("user@naver.com", "12344");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }
}
