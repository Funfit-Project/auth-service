package funfit.auth.auth.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.auth.dto.JoinRequest;
import funfit.auth.auth.dto.JoinResponse;
import funfit.auth.auth.dto.LoginRequest;
import funfit.auth.auth.dto.LoginResponse;
import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.rabbitMq.dto.CreateNewMemberPubDto;
import funfit.auth.rabbitMq.service.RabbitMqService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired private UserRepository userRepository;
    @Autowired private RabbitTemplate rabbitTemplate;
    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = new AuthService(userRepository, new RabbitMqServiceStub(rabbitTemplate));
    }

    class RabbitMqServiceStub extends RabbitMqService {

        public RabbitMqServiceStub(RabbitTemplate rabbitTemplate) {
            super(rabbitTemplate);
        }

        @Override
        public void publishCreateNewMember(CreateNewMemberPubDto createNewMemberPubDto) {
        }
    }

    @Test
    @DisplayName("회원가입 성공-트레이너")
    void joinTrainerSuccess() {
        // given
        JoinRequest joinRequest = JoinRequest.builder()
                .email("trainer@naver.com")
                .password("1234")
                .name("trainer")
                .role("트레이너")
                .phoneNumber("01012345678")
                .build();

        // when
        JoinResponse joinResponse = authService.join(joinRequest);

        // then
        Assertions.assertThat(joinResponse.getEmail()).isEqualTo("trainer@naver.com");
        Assertions.assertThat(joinResponse.getName()).isEqualTo("trainer");
        Assertions.assertThat(joinResponse.getRole()).isEqualTo("트레이너");
        Assertions.assertThat(joinResponse.getTrainerName()).isNull();
        Assertions.assertThat(joinResponse.getCenterName()).isNull();
        Assertions.assertThat(joinResponse.getRegistrationCount()).isNull();
    }

    @Test
    @DisplayName("회원가입 성공-회원")
    void joinMemberSuccess() {
        // given
        User trainer = User.create("trainer@naver.com", "1234", "trainer", Role.TRAINER, "01011112222");
        userRepository.save(trainer);

        // when
        JoinRequest joinRequest = JoinRequest.builder()
                .email("member@naver.com")
                .password("1234")
                .name("member")
                .role("회원")
                .phoneNumber("01012345678")
                .userCode(trainer.getUserCode())
                .centerName("석세스짐")
                .registrationCount(10)
                .build();

        JoinResponse joinResponse = authService.join(joinRequest);

        // then
        Assertions.assertThat(joinResponse.getEmail()).isEqualTo("member@naver.com");
        Assertions.assertThat(joinResponse.getName()).isEqualTo("member");
        Assertions.assertThat(joinResponse.getRole()).isEqualTo("회원");
        Assertions.assertThat(joinResponse.getTrainerName()).isEqualTo("trainer");
        Assertions.assertThat(joinResponse.getCenterName()).isEqualTo("석세스짐");
        Assertions.assertThat(joinResponse.getRegistrationCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("회원가입 실패-회원 필수값 누락")
    void joinMemberFailByRequiredData() {
        // given
        JoinRequest joinRequestExcludeUserCode = JoinRequest.builder()
                .email("member@naver.com")
                .password("1234")
                .name("member")
                .role("회원")
                .phoneNumber("01012345678")
                .centerName("석세스짐")
                .registrationCount(10)
                .build();

        JoinRequest joinRequestExcludeCenterName = JoinRequest.builder()
                .email("member@naver.com")
                .password("1234")
                .name("member")
                .role("회원")
                .phoneNumber("01012345678")
                .userCode("userCode")
                .registrationCount(10)
                .build();

        JoinRequest joinRequestExcludeRegistrationCount = JoinRequest.builder()
                .email("member@naver.com")
                .password("1234")
                .name("member")
                .role("회원")
                .phoneNumber("01012345678")
                .userCode("userCode")
                .centerName("석세스짐")
                .build();

        // then
        Assertions.assertThat(assertThrows(BusinessException.class, () -> authService.join(joinRequestExcludeUserCode)).getErrorCode())
                .isEqualTo(ErrorCode.REQUIRED_USER_CODE);

        Assertions.assertThat(assertThrows(BusinessException.class, () -> authService.join(joinRequestExcludeCenterName)).getErrorCode())
                .isEqualTo(ErrorCode.REQUIRED_CENTER_NAME);

        Assertions.assertThat(assertThrows(BusinessException.class, () -> authService.join(joinRequestExcludeRegistrationCount)).getErrorCode())
                .isEqualTo(ErrorCode.REQUIRED_REGISTRATION_COUNT);
    }

    @Test
    @DisplayName("회원가입 실패-이메일 중복")
    void joinFailByDuplicatedEmail() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user", Role.MEMBER, "01012345678"));

        // when
        JoinRequest joinRequest = JoinRequest.builder()
                .email("user@naver.com")
                .password("1234")
                .name("user")
                .role("트레이너")
                .phoneNumber("01012345678")
                .build();

        // then
        Assertions.assertThat(assertThrows(BusinessException.class, () -> authService.join(joinRequest)).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));

        // when
        LoginRequest loginRequest = new LoginRequest("user@naver.com", "1234");
        LoginResponse loginResponse = authService.login(loginRequest);

        // then
        Assertions.assertThat(loginResponse).isNotNull();
        Assertions.assertThat(loginResponse.getEmail()).isEqualTo("user@naver.com");
    }

    @Test
    @DisplayName("로그인 실패-이메일 오류")
    void loginFailByEmail() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));
        LoginRequest loginRequest = new LoginRequest("userr@naver.com", "1234");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_EMAIL);
    }

    @Test
    @DisplayName("로그인 실패-패스워드 오류")
    void loginFailByPassword() {
        // given
        userRepository.save(User.create("user@naver.com", "1234", "user1", Role.MEMBER, "01012345678"));
        LoginRequest loginRequest = new LoginRequest("user@naver.com", "12344");

        // then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }
}
