package funfit.auth.userInfo.service;

import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.userInfo.dto.EditUserInfoRequest;
import funfit.auth.userInfo.dto.ReadUserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserInfoServiceTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("사용자 정보 조회 성공")
    public void readUserInfoSuccess() {
        // given
        String email = "user@naver.com";
        userRepository.save(User.create(email, "1234", "user", Role.MEMBER, "01012345678"));
        User user = userRepository.findByEmail(email).get();

        // when
        ReadUserResponse readUserResponse = userInfoService.readUserInfo("user@naver.com");

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
        // given
        String email = "user@naver.com";
        userRepository.save(User.create(email, "1234", "user", Role.MEMBER, "01012345678"));

        // when
        EditUserInfoRequest editUserInfoRequest = new EditUserInfoRequest("editedName");
        ReadUserResponse readUserResponse = userInfoService.editUserInfo(editUserInfoRequest, email);

        // then
        User user = userRepository.findByEmail(email).get();

        Assertions.assertThat(readUserResponse.getId()).isEqualTo(user.getId());
        Assertions.assertThat(readUserResponse.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(readUserResponse.getName()).isEqualTo(user.getName());
        Assertions.assertThat(readUserResponse.getRoleName()).isEqualTo(user.getRole().getName());
        Assertions.assertThat(readUserResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        Assertions.assertThat(readUserResponse.getUserCode()).isEqualTo(user.getUserCode());
    }
}
