package funfit.auth.rabbitMq.service;

import funfit.auth.rabbitMq.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    /**
     * PT 회원 회원가입 시 메시지 발행
     */
    @Async
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5, maxDelay = 5000))
    public void publishCreateNewMember(CreateNewMemberPubDto createNewMemberPubDto) {
        log.info("RabbitMQ | publish message, queue name = create_new_member, message = {}", createNewMemberPubDto.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, "create_new_member", createNewMemberPubDto);
        } catch (Exception e) {
            log.error("MQ 메시지 발행 실패, retry 시도");
            throw e;
        }
    }

    /**
     * 회원 정보 변경 시 데이터 동기화를 위해 메시지 발행
     */
    @Async
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5, maxDelay = 5000))
    public void publishEditedUserEmail(String email) {
        log.info("RabbitMQ | publish message, queue name = edited_user_email, message = {}", email);
        try {
            rabbitTemplate.convertAndSend(exchange, "edited_user_email_for_pt", email);
            rabbitTemplate.convertAndSend(exchange, "edited_user_email_for_community", email);
        } catch (Exception e) {
            log.error("MQ 메시지 발행 실패, retry 시도");
            throw e;
        }
    }
}
