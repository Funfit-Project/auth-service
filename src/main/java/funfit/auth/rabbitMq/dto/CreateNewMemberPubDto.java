package funfit.auth.rabbitMq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateNewMemberPubDto {

    private long memberId;
    private long trainerId;
    private String centerName;
    private int registrationCount;
}
