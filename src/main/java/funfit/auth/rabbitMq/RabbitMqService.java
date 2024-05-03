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
import org.springframework.amqp.core.MessageProperties;
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
    private final ObjectMapper mapper;

    @RabbitListener(queues = "request_user_by_email")
    public Message onMessageInUserRequestByEmail(Message message) throws JsonProcessingException {
        RequestUserByEmail dto = mapper.readValue(new String(message.getBody()), RequestUserByEmail.class);
        log.info("RabbitMQ | on message in request_user_by_email, message = {}", dto.toString());
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getPassword(), user.getEmail(), user.getRole().getName(), user.getPhoneNumber(), user.getUserCode());

        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        properties.setCorrelationId(message.getMessageProperties().getCorrelationId());

        return MessageBuilder.withBody(mapper.writeValueAsString(userDto).getBytes())
                .andProperties(properties)
                .build();
    }

    @RabbitListener(queues = "request_validate_trainer_code")
    public Message onMessageInRequestValidateTrainerCode(Message message) throws JsonProcessingException {
        RequestValidateTrainerCode dto = mapper.readValue(new String(message.getBody()), RequestValidateTrainerCode.class);
        Optional<User> optionalUser = userRepository.findByUserCode(dto.getTrainerCode());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(true, user.getId(), user.getName(), user.getUserCode());
            MessageProperties properties = new MessageProperties();
            properties.setContentType("application/json");
            properties.setCorrelationId(message.getMessageProperties().getCorrelationId());
            return MessageBuilder.withBody(mapper.writeValueAsString(response).getBytes())
                    .andProperties(properties)
                    .build();
        } else {
            ResponseValidateTrainerCode response = new ResponseValidateTrainerCode(false, -1, null, dto.getTrainerCode());
            MessageProperties properties = new MessageProperties();
            properties.setContentType("application/json");
            properties.setCorrelationId(message.getMessageProperties().getCorrelationId());
            return MessageBuilder.withBody(mapper.writeValueAsString(response).getBytes())
                    .andProperties(properties)
                    .build();
        }
    }
}
