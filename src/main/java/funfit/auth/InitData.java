package funfit.auth;

import funfit.auth.user.entity.Role;
import funfit.auth.user.entity.User;
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
        User user1 = User.create("user1@naver.com", "1234", "user1",
                Role.MEMBER, "01011112222");

        User user2 = User.create("user2@naver.com", "1234", "user2",
                Role.TRAINER, "01011112222");

        userRepository.save(user1);
        userRepository.save(user2);
    }
}
