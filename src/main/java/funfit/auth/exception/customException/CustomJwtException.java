package funfit.auth.exception.customException;

import funfit.auth.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class
CustomJwtException extends RuntimeException {

    private final ErrorCode errorCode;
}
