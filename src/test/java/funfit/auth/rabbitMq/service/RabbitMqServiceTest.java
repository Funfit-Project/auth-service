package funfit.auth.rabbitMq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.rabbitMq.dto.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RabbitMqServiceTest {

    @Autowired
    private RabbitMqService rabbitMqService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void initUser() {
        User member = User.create("member@naver.com", "1234", "member", Role.MEMBER, "01012345678");
        userRepository.save(member);

        User trainer = User.create("trainer@naver.com", "1234", "trainer", Role.TRAINER, "01012345678");
        userRepository.save(trainer);
    }

    @Test
    @DisplayName("user_request_by_email 큐에 메시지가 도착했을 때 메시지 반환 성공-pt 서비스")
    public void onMessageInUserRequestByEmailTesSuccessFotPt() throws IOException {
        // given
        RequestUserByEmail requestDto = new RequestUserByEmail("member@naver.com", "pt");

        // when
        Message message = rabbitMqService.onMessageInUserRequestByEmail(requestDto);

        // then
        ResponseUserDtoForPt responseDto = mapper.readValue(message.getBody(), ResponseUserDtoForPt.class);
        User user = userRepository.findByEmail("member@naver.com").get();

        Assertions.assertThat(responseDto.getUserId()).isEqualTo(user.getId());
        Assertions.assertThat(responseDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(responseDto.getUserName()).isEqualTo(user.getName());
        Assertions.assertThat(responseDto.getRoleName()).isEqualTo(user.getRole().getName());
        Assertions.assertThat(responseDto.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        Assertions.assertThat(responseDto.getUserCode()).isEqualTo(user.getUserCode());
    }

    @Test
    @DisplayName("user_request_by_email 큐에 메시지가 도착했을 때 메시지 반환 성공-community 서비스")
    public void onMessageInUserRequestByEmailTestSuccessForCommunity() throws IOException {
        // given
        RequestUserByEmail requestDto = new RequestUserByEmail("member@naver.com", "community");

        // when
        Message message = rabbitMqService.onMessageInUserRequestByEmail(requestDto);

        // then
        ResponseUserDtoForCommunity responseDto = mapper.readValue(message.getBody(), ResponseUserDtoForCommunity.class);
        User user = userRepository.findByEmail("member@naver.com").get();

        Assertions.assertThat(responseDto.getUserId()).isEqualTo(user.getId());
        Assertions.assertThat(responseDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(responseDto.getUserName()).isEqualTo(user.getName());
        Assertions.assertThat(responseDto.getRoleName()).isEqualTo(user.getRole().getName());
    }

    @Test
    @DisplayName("user_request_by_email 큐에 메시지가 도착했을 때 메시지 반환 실패-존재하지 않는 이메일")
    public void onMessageInUserRequestByEmailTestFailByInvalidEmail() {
        // given
        RequestUserByEmail requestDto = new RequestUserByEmail("memberr@naver.com", "pt");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            rabbitMqService.onMessageInUserRequestByEmail(requestDto);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_EMAIL);
    }

    @Test
    @DisplayName("user_request_by_email 큐에 메시지가 도착했을 때 메시지 반환 실패-존재하지 않는 서비스명")
    public void onMessageInUserRequestByEmailTestFailByInvalidRequestServiceName() {
        // given
        RequestUserByEmail requestDto = new RequestUserByEmail("member@naver.com", "ptt");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            rabbitMqService.onMessageInUserRequestByEmail(requestDto);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST_SERVICE_NAME);
    }
}
