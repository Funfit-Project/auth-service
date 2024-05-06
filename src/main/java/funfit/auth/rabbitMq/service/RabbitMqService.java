package funfit.auth.rabbitMq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.rabbitMq.dto.*;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "request_user_by_email")
    public Message onMessageInUserRequestByEmail(RequestUserByEmail requestDto) throws JsonProcessingException {
        log.info("RabbitMQ | on message in request_user_by_email queue, message = {}", requestDto.toString());

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));

        if (requestDto.getRequestServiceName().equals("pt")) {
            ResponseUserDtoForPt responseDto = new ResponseUserDtoForPt(user.getId(), user.getEmail(), user.getName(), user.getRole().getName(), user.getPhoneNumber(), user.getUserCode());
            return MessageBuilder
                    .withBody(mapper.writeValueAsString(responseDto).getBytes())
                    .build();
        }
        if (requestDto.getRequestServiceName().equals("community")) {
            ResponseUserDtoForCommunity responseDto = new ResponseUserDtoForCommunity(user.getId(), user.getEmail(), user.getName(), user.getRole().getName());
            return MessageBuilder
                    .withBody(mapper.writeValueAsString(responseDto).getBytes())
                    .build();
        }

        throw new RuntimeException("RabbitMQ 오류 발생");
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

    public void publishEditUserId(long userId) {
        rabbitTemplate.convertAndSend("edited_user_id_for_pt", userId);
        rabbitTemplate.convertAndSend("edited_user_id_for_community", userId);
    }
}
