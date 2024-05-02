package funfit.auth.rabbitMq;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.rabbitMq.dto.RequestUserByEmail;
import funfit.auth.rabbitMq.dto.RequestValidateTrainerCode;
import funfit.auth.rabbitMq.dto.ResponseValidateTrainerCode;
import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    @RabbitListener(queues = "user_request_by_email")
    public void onMessageInUserRequestByEmail(final RequestUserByEmail dto) {
        log.info("RabbitMQ| on message in user request by email, user email = {}", dto.getEmail());
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
        rabbitTemplate.convertAndSend(dto.getResponseQueue(), user);
        log.info("RabbitMQ| success send messages");
    }

    @RabbitListener(queues = "request_validate_trainer_code")
    public void onMessageInRequestValidateTrainerCode(final RequestValidateTrainerCode dto) {
        log.info("RabbitMQ| on message in request validate trainer code, trainer code = {}", dto.getTrainerCode());
        Optional<User> optionalUser = userRepository.findByUserCode(dto.getTrainerCode());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(true, user.getId(), user.getName(), user.getUserCode());
            rabbitTemplate.convertAndSend(dto.getResponseQueue(), response);
        } else {
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(false, -1, null, dto.getTrainerCode());
            rabbitTemplate.convertAndSend(dto.getResponseQueue(), response);
        }
        log.info("RabbitMQ| success send messages");
    }
}
