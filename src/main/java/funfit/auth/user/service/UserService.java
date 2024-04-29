package funfit.auth.user.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.exception.utils.JwtUtils;
import funfit.auth.user.dto.JoinRequest;
import funfit.auth.user.dto.JoinResponse;
import funfit.auth.user.dto.JwtDto;
import funfit.auth.user.dto.LoginRequest;
import funfit.auth.user.entity.Role;
import funfit.auth.user.entity.User;
import funfit.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

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

    public JwtDto login(LoginRequest loginRequest) {
        validateEmailPassword(loginRequest);
        return jwtUtils.generateJwt(loginRequest.getEmail());
    }

    private void validateEmailPassword(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
