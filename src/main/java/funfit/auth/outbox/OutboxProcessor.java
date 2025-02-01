package funfit.auth.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import funfit.auth.kafka.KafkaService;
import funfit.auth.kafka.PtMemberJoinedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    public void publishOutboxMessage() throws JsonProcessingException {
        List<Outbox> outboxes = outboxRepository.findAll();
        for (Outbox outbox : outboxes) {
            if (outbox.getTopic().equals("user-info-updated")) {
                kafkaService.publishUserInfoUpdated(outbox.getPayload());
            }
            if (outbox.getTopic().equals("pt-member-joined")) {
                kafkaService.publishPtMemberJoined(objectMapper.readValue(outbox.getPayload(), PtMemberJoinedDto.class));
            }
            outboxRepository.delete(outbox);
        }
    }
}
