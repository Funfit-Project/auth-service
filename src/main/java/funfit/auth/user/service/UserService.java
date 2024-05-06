package funfit.auth.user.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.exception.utils.JwtUtils;
import funfit.auth.mypage.dto.EditUserInfoRequest;
import funfit.auth.mypage.dto.ReadUserResponse;
import funfit.auth.rabbitMq.RabbitMqService;
import funfit.auth.user.dto.*;
import funfit.auth.user.entity.Role;
import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RabbitMqService rabbitMqService;

    public JoinResponse join(JoinRequest joinRequest) {
        validateDuplicate(joinRequest.getEmail());
        User user = User.create(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getName(),
                Role.find(joinRequest.getRole()), joinRequest.getPhoneNumber());
        userRepository.save(user);
        return new JoinResponse(user.getEmail(), user.getName(), user.getRole().getName());
    }

    private void validateDuplicate(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) {
        validateEmailPassword(loginRequest);
        return jwtUtils.generateJwt(loginRequest.getEmail());
    }

    private void validateEmailPassword(LoginRequest loginRequest) {
        User user = findUser(loginRequest.getEmail());
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public ReadUserResponse readUserInfo(HttpServletRequest request) {
        String email = jwtUtils.getEmailFromHeader(request);
        User user = findUser(email);
        return new ReadUserResponse(user);
    }

    public ReadUserResponse editUserInfo(EditUserInfoRequest dto, HttpServletRequest request) {
        String email = jwtUtils.getEmailFromHeader(request);
        User user = findUser(email);
        user.editUserInfo(dto.getName());

        rabbitMqService.publishEditUserId(user.getId());
        return new ReadUserResponse(user);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
    }
}
