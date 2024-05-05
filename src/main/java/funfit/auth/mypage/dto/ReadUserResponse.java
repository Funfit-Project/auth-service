package funfit.auth.mypage.dto;

import funfit.auth.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadUserResponse {

    private long id;
    private String email;
    private String name;
    private String roleName;
    private String phoneNumber;
    private String userCode;

    public ReadUserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.roleName = user.getRole().getName();
        this.phoneNumber = user.getPhoneNumber();
        this.userCode = user.getUserCode();
    }
}
