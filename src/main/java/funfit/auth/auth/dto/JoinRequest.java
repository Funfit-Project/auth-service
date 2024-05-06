package funfit.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JoinRequest {

    private String email;
    private String password;
    private String name;
    private String role;
    private String phoneNumber;
}
