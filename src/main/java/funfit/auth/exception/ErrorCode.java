package funfit.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    REQUIRED_USER_CODE(HttpStatus.BAD_REQUEST, "트레이너의 회원코드는 필수입니다."),
    REQUIRED_CENTER_NAME(HttpStatus.BAD_REQUEST, "센터명은 필수입니다."),
    REQUIRED_REGISTRATION_COUNT(HttpStatus.BAD_REQUEST, "등록횟수는 필수입니다."),

    INVALID_USER_CODE(HttpStatus.BAD_REQUEST, "잘못된 회원코드입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "가입되지 않은 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 패스워드입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "잘못된 사용자 역할입니다."),

    // rabbitmq
    INVALID_REQUEST_SERVICE_NAME(HttpStatus.BAD_REQUEST, "잘못된 서비스명입니다."),

    // jwt
    EXPIRED_JWT(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    REQUIRED_JWT(HttpStatus.BAD_REQUEST, "토큰이 입력되지 않았습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
