package funfit.auth.auth.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.auth.dto.*;
import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

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
        return new LoginResponse(loginRequest.getEmail());
    }

    private void validateEmailPassword(LoginRequest loginRequest) {
        User user = findUser(loginRequest.getEmail());
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
    }
}
