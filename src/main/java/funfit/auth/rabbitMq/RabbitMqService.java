package funfit.auth.rabbitMq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.rabbitMq.dto.RequestUserByEmail;
import funfit.auth.rabbitMq.dto.RequestValidateTrainerCode;
import funfit.auth.rabbitMq.dto.ResponseValidateTrainerCode;
import funfit.auth.rabbitMq.dto.UserDto;
import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    @RabbitListener(queues = "request_user_by_email")
    public Message onMessageInUserRequestByEmail(RequestUserByEmail requestUserByEmail) throws JsonProcessingException {
        log.info("RabbitMQ | on message in request_user_by_email, message = {}", requestUserByEmail.toString());
        User user = userRepository.findByEmail(requestUserByEmail.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getPassword(), user.getEmail(), user.getRole().getName(), user.getPhoneNumber(), user.getUserCode());

        return MessageBuilder
                .withBody(mapper.writeValueAsString(userDto).getBytes())
                .build();
    }

    @RabbitListener(queues = "request_validate_trainer_code")
    public Message onMessageInRequestValidateTrainerCode(RequestValidateTrainerCode requestValidateTrainerCode) throws JsonProcessingException {
        log.info("RabbitMQ | on message in request_validate_trainer_code, message = {}", requestValidateTrainerCode.toString());

        Optional<User> optionalUser = userRepository.findByUserCode(requestValidateTrainerCode.getTrainerCode());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(true, user.getId(), user.getName(), requestValidateTrainerCode.getTrainerCode());
            return MessageBuilder.withBody(mapper.writeValueAsString(response).getBytes())
                    .build();
        } else {
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(false, -1, null, requestValidateTrainerCode.getTrainerCode());
            return MessageBuilder
                    .withBody(mapper.writeValueAsString(response).getBytes())
                    .build();
        }
    }
}
