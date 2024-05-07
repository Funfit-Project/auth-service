package funfit.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "가입되지 않은 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 패스워드입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "잘못된 사용자 역할입니다."),

    // rabbitmq
    INVALID_REQUEST_SERVICE_NAME(HttpStatus.BAD_REQUEST, "잘못된 서비스명입니다."),

    // jwt
    REQUIRED_JWT(HttpStatus.BAD_REQUEST, "토큰이 입력되지 않았습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
