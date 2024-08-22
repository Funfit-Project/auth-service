package funfit.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JoinResponse {

    private String email;
    private String name;
    private String role;
    private String trainerName;
    private String centerName;
    private Integer registrationCount;
}
