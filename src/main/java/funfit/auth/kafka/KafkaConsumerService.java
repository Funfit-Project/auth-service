package funfit.auth.kafka;

import funfit.auth.kafka.dto.DeductAndCompensatePoints;
import funfit.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(
            topics = "compensate-points",
            groupId = "auth-service-group",
            containerFactory = "kafkaListenerContainerFactoryForCompensatePointsDto"
    )
    public void consumeCompensatePoints(DeductAndCompensatePoints deductAndCompensatePoints, Acknowledgment acknowledgment) {
        log.info("kafka consume compensate-points, message = {}", deductAndCompensatePoints.toString());
        userService.compensatePoints(deductAndCompensatePoints);
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "compensate-points.DLT",
            groupId = "auth-service-group",
            containerFactory = "kafkaListenerContainerFactoryForCompensatePointsDto"
    )
    public void consumeCompensatePointsDLQ(DeductAndCompensatePoints deductAndCompensatePoints, Acknowledgment acknowledgment) {
        log.info("kafka consume compensate-points.DLT, message = {}", deductAndCompensatePoints.toString());
        kafkaProducerService.publishCompensatePoints(deductAndCompensatePoints);
        acknowledgment.acknowledge();
    }
}
