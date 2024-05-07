package funfit.auth.utils;

import funfit.auth.auth.dto.JwtDto;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("JWT 토큰 생성 성공")
    public void generateJwtSuccess() {
        // given
        JwtDto jwtDto = jwtUtils.generateJwt("user@naver.com");

        // then
        Assertions.assertThat(jwtDto.getAccessToken()).isNotNull();
        Assertions.assertThat(jwtDto.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("HTTP 요청 헤더에서 이메일 추출 성공")
    public void getEmailFromHeaderSuccess() {
        // given
        JwtDto jwtDto = jwtUtils.generateJwt("user@naver.com");
        String accessToken = jwtDto.getAccessToken();

        // when
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization",  "Bearer " + accessToken);

        // then
        String email = jwtUtils.getEmailFromHeader(request);
        Assertions.assertThat(email).isEqualTo("user@naver.com");
    }

    @Test
    @DisplayName("HTTP 요청 헤더에서 이메일 추출 실패-토큰 누락")
    public void getEmailFromHeaderFail() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtUtils.getEmailFromHeader(request);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_JWT);
    }
}
