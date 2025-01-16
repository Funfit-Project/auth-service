package funfit.auth;

import funfit.auth.entity.Role;
import funfit.auth.entity.User;
import funfit.auth.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        User user = User.create("trainer" + "@naver.com", "1234", "trainer", Role.TRAINER, "01011112222");
        userRepository.save(user);

//        String userCode = user.getUserCode();
//
//        for (int i = 1; i <= 10; i++) {
//            userRepository.save(User.create("user" + i + "@naver.com", "1234", "user" + i, Role.MEMBER, "01011112222"));
//        }
    }

    @PostConstruct
    public void joinUser() {

        for (int i = 1; i <= 1000; i++) {
            User user = User.create("user" + i + "@naver.com", "1234", "user" + i, Role.MEMBER, "01011112222");
            userRepository.save(user);
        }
    }
}
