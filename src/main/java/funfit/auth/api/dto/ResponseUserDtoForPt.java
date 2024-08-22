package funfit.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDtoForPt {

    private long userId;
    private String email;
    private String userName;
    private String roleName;
    private String phoneNumber;
    private String userCode;
}
