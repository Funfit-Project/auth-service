package funfit.auth.rabbitMq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDtoForCommunity {

//    private boolean isSuccess;

    private long userId;
    private String email;
    private String userName;
    private String roleName;
}
