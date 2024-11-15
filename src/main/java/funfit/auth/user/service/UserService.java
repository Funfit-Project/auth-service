package funfit.auth.user.service;

import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.user.dto.EditUserInfoRequest;
import funfit.auth.user.dto.ReadUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public ReadUserResponse readUserInfo(String email) {
        User user = findUser(email);
        return new ReadUserResponse(user);
    }

    public ReadUserResponse editUserInfo(EditUserInfoRequest dto, String email) {
        User user = findUser(email);
        user.editUserInfo(dto.getName());
        return new ReadUserResponse(user);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
    }
}
