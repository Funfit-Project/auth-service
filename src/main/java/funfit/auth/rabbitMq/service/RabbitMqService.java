package funfit.auth.rabbitMq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import funfit.auth.MicroServiceName;
import funfit.auth.rabbitMq.dto.ErrorResponse;
import funfit.auth.rabbitMq.dto.MqResult;
import funfit.auth.rabbitMq.dto.*;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
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
        Optional<User> optionalUser = userRepository.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("회원을 찾을 수 없습니다.");
            MqResult<ErrorResponse> mqResult = new MqResult<>(false, HttpStatus.NOT_FOUND, errorResponse);

            return MessageBuilder
                    .withBody(mapper.writeValueAsString(mqResult).getBytes())
                    .build();
        }
        User user = optionalUser.get();
        if (requestDto.getServiceName() == MicroServiceName.PT) {
            ResponseUserDtoForPt responseDto = new ResponseUserDtoForPt(user.getId(), user.getEmail(), user.getName(), user.getRole().getName(), user.getPhoneNumber(), user.getUserCode());
            MqResult<ResponseUserDtoForPt> mqResult = new MqResult<>(true, HttpStatus.OK, responseDto);
            return MessageBuilder
                    .withBody(mapper.writeValueAsString(mqResult).getBytes())
                    .build();
        }

        if (requestDto.getServiceName() == MicroServiceName.COMMUNITY) {
            ResponseUserDtoForCommunity responseDto = new ResponseUserDtoForCommunity(user.getId(), user.getEmail(), user.getName(), user.getRole().getName());
            MqResult<ResponseUserDtoForCommunity> mqResult = new MqResult<>(true, HttpStatus.OK, responseDto);

            return MessageBuilder
                    .withBody(mapper.writeValueAsString(mqResult).getBytes())
                    .build();
        }

        ErrorResponse errorResponse = new ErrorResponse("요청 서비스가 잘못되었습니다.");
        MqResult<ErrorResponse> mqResult = new MqResult<>(false, HttpStatus.BAD_REQUEST, errorResponse);

        return MessageBuilder
                .withBody(mapper.writeValueAsString(mqResult).getBytes())
                .build();
    }

    public void publishEditUserId(long userId) {
        rabbitTemplate.convertAndSend("edited_user_id_for_pt", userId);
        rabbitTemplate.convertAndSend("edited_user_id_for_community", userId);
    }

    public void publishCreateNewMember(CreateNewMemberPubDto createNewMemberPubDto) {
        log.info("RabbitMQ | publish message in create_new_member queue, message = {}", createNewMemberPubDto.toString());
        rabbitTemplate.convertAndSend("create_new_member", createNewMemberPubDto);
    }
}
