package funfit.auth.rabbitMq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class CreateNewMemberPubDto {

    private String memberEmail;
    private String trainerEmail;
    private String centerName;
    private int registrationCount;
}
