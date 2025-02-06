package funfit.auth.kafka;

import funfit.auth.kafka.dto.DeductAndCompensatePoints;
import funfit.auth.kafka.dto.PtMemberJoinedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplateForString;
    private final KafkaTemplate<String, PtMemberJoinedDto> kafkaTemplateForPtMemberJoinedDto;
    private final KafkaTemplate<String, DeductAndCompensatePoints> kafkaTemplateForCompensatePointsDto;

    /**
     * 회원 정보 변경 시 데이터 동기화를 위해 메시지 발행
     */
    public void publishUserInfoUpdated(String email) {
        log.info("kafka publish message, message = {}", email);
        kafkaTemplateForString.send("user-info-updated", email);
    }

    /**
     * PT 회원 회원가입 시 메시지 발행
     */
    public void publishPtMemberJoined(PtMemberJoinedDto ptMemberJoinedDto) {
        log.info("kafka publish message, message = {}", ptMemberJoinedDto);
        kafkaTemplateForPtMemberJoinedDto.send("pt-member-joined", ptMemberJoinedDto);
    }

    public void publishCompensatePoints(DeductAndCompensatePoints deductAndCompensatePoints) {
        log.info("kafka publish compensate-points, message = {}", deductAndCompensatePoints.toString());
        kafkaTemplateForCompensatePointsDto.send("compensate-points", deductAndCompensatePoints);
    }
}
