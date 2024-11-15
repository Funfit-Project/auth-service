package funfit.auth.user.service;

import funfit.auth.user.entity.Role;
import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import funfit.auth.rabbitMq.service.MessageRetryService;
import funfit.auth.rabbitMq.service.RabbitMqService;
import funfit.auth.user.dto.EditUserInfoRequest;
import funfit.auth.user.dto.ReadUserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRetryService messageRetryService;

    @Test
    @DisplayName("사용자 정보 조회 성공")
    public void readUserInfoSuccess() {
        UserService userService = new UserService(userRepository);

        // given
        String email = "user@naver.com";
        userRepository.save(User.create(email, "1234", "user", Role.MEMBER, "01012345678"));
        User user = userRepository.findByEmail(email).get();

        // when
        ReadUserResponse readUserResponse = userService.readUserInfo("user@naver.com");

        // then
        Assertions.assertThat(readUserResponse.getId()).isEqualTo(user.getId());
        Assertions.assertThat(readUserResponse.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(readUserResponse.getName()).isEqualTo(user.getName());
        Assertions.assertThat(readUserResponse.getRoleName()).isEqualTo(user.getRole().getName());
        Assertions.assertThat(readUserResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        Assertions.assertThat(readUserResponse.getUserCode()).isEqualTo(user.getUserCode());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    public void editUserInfoSuccess() {
        UserService userService = new UserService(userRepository);

        // given
        String email = "user@naver.com";
        userRepository.save(User.create(email, "1234", "user", Role.MEMBER, "01012345678"));

        // when
        EditUserInfoRequest editUserInfoRequest = new EditUserInfoRequest("editedName");
        ReadUserResponse readUserResponse = userService.editUserInfo(editUserInfoRequest, email);

        // then
        User user = userRepository.findByEmail(email).get();

        Assertions.assertThat(readUserResponse.getId()).isEqualTo(user.getId());
        Assertions.assertThat(readUserResponse.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(readUserResponse.getName()).isEqualTo(user.getName());
        Assertions.assertThat(readUserResponse.getRoleName()).isEqualTo(user.getRole().getName());
        Assertions.assertThat(readUserResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        Assertions.assertThat(readUserResponse.getUserCode()).isEqualTo(user.getUserCode());
    }

    class StubRabbitMqService extends RabbitMqService {

        public StubRabbitMqService(RabbitTemplate rabbitTemplate) {
            super(rabbitTemplate, messageRetryService);
        }

        @Override
        public void publishEditedUserEmail(String email) {
        }
    }
}
