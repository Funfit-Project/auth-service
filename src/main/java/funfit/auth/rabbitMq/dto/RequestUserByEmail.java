package funfit.auth.rabbitMq.dto;

import funfit.auth.MicroServiceName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserByEmail {

    private String email;
    private MicroServiceName serviceName;
}
