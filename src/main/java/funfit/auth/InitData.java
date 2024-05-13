package funfit.auth;

import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData {

    private final UserRepository userRepository;
//
//    @PostConstruct
//    public void initPost() {
//        for (int i = 1; i <= 10; i++) {
//            User user = User.create("user" + i + "@naver.com", "1234", "user" + i, Role.MEMBER, "01011112222");
//            userRepository.save(user);
//        }
//    }
}
