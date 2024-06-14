package funfit.auth.userInfo.service;

import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.userInfo.dto.EditUserInfoRequest;
import funfit.auth.userInfo.dto.ReadUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInfoService {

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
