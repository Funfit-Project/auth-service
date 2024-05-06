package funfit.auth.rabbitMq.service;

import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.rabbitMq.dto.ResponseUserDtoForCommunity;
import funfit.auth.rabbitMq.dto.ResponseUserDtoForPt;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RabbitMqUserServiceTest {

    @Autowired
    private RabbitMqUserService rabbitMqUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void init() {
        em.createNativeQuery("alter table users auto_increment = 1;")
                .executeUpdate();

        User user = User.create("user@naver.com", "1234", "user", Role.MEMBER, "01012345678");
        userRepository.save(user);
    }

    @Test
    @DisplayName("사용자 id를 통해 사용자 정보 반환 성공(for pt 서비스)")
    public void requestUserInfoFotPtSuccess() {
        // when
        ResponseUserDtoForPt responseUserDtoForPt = rabbitMqUserService.requestUserInfoFotPt(1);

        // then
        Assertions.assertThat(responseUserDtoForPt.getUserId()).isEqualTo(1);
        Assertions.assertThat(responseUserDtoForPt.getEmail()).isEqualTo("user@naver.com");
        Assertions.assertThat(responseUserDtoForPt.getUserName()).isEqualTo("user");
        Assertions.assertThat(responseUserDtoForPt.getRoleName()).isEqualTo(Role.MEMBER.getName());
        Assertions.assertThat(responseUserDtoForPt.getPhoneNumber()).isEqualTo("01012345678");
    }

    @Test
    @DisplayName("사용자 id를 통해 사용자 정보 반환 실패(for pt 서비스)-존재하지 않는 id")
    public void requestUserInfoFotPtFail() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            rabbitMqUserService.requestUserInfoFotPt(2);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("사용자 id를 통해 사용자 정보 반환 성공(for community 서비스)")
    public void requestUserInfoFotCommunitySuccess() {
        // when
        ResponseUserDtoForCommunity responseUserDtoForCommunity = rabbitMqUserService.requestUserInfoFotCommunity(1);

        // then
        Assertions.assertThat(responseUserDtoForCommunity.getUserId()).isEqualTo(1);
        Assertions.assertThat(responseUserDtoForCommunity.getEmail()).isEqualTo("user@naver.com");
        Assertions.assertThat(responseUserDtoForCommunity.getUserName()).isEqualTo("user");
        Assertions.assertThat(responseUserDtoForCommunity.getRoleName()).isEqualTo(Role.MEMBER.getName());
    }

    @Test
    @DisplayName("사용자 id를 통해 사용자 정보 반환 실패(for community 서비스)-존재하지 않는 id")
    public void requestUserInfoFotCommunityFail() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            rabbitMqUserService.requestUserInfoFotCommunity(2);
        });
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_USER);
    }
}
