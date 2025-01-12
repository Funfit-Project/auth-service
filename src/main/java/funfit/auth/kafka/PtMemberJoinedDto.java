package funfit.auth.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class PtMemberJoinedDto {

    private String memberEmail;
    private String trainerEmail;
    private String centerName;
    private int registrationCount;
}
