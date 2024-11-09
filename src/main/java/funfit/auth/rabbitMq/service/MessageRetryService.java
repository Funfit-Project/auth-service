package funfit.auth.rabbitMq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageRetryService {

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;
    private final BlockingQueue<String> pendingMessageQueue = new LinkedBlockingQueue<>();
    private final RabbitTemplate rabbitTemplate;
    private boolean isConnected = false;

    public void saveMessage(String message) {
        log.info("미전송 메시지 저장");
        pendingMessageQueue.offer(message);
    }

    @Scheduled(fixedDelay = 3000)
    public void checkAndRepublish() {
        if (!isConnected && rabbitTemplate.getConnectionFactory().createConnection().isOpen()) {
            log.info("RabbitMQ 서버가 재연결 성공. 미전송 메시지 재발행 시도.");
            isConnected = true;
            republishEditedUserEmail();
        } else if (!rabbitTemplate.getConnectionFactory().createConnection().isOpen()) {
            isConnected = false;
        }
    }

    public void republishEditedUserEmail() {
        while (!pendingMessageQueue.isEmpty()) {
            String email = pendingMessageQueue.poll();
            log.info("전송 실패 메시지 재처리, message = {}", email);
            rabbitTemplate.convertAndSend(exchange, "edited_user_email_for_pt", email);
            rabbitTemplate.convertAndSend(exchange, "edited_user_email_for_community", email);
        }
    }
}
