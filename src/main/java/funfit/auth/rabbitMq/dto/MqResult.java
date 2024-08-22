package funfit.auth.rabbitMq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MqResult<T> {

    private boolean success;
    private HttpStatus httpStatus;
    private T result;
}
