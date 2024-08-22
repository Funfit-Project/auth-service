package funfit.auth.auth.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.auth.dto.*;
import funfit.auth.auth.entity.Role;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.rabbitMq.dto.CreateNewMemberPubDto;
import funfit.auth.rabbitMq.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RabbitMqService rabbitMqService;

    public JoinResponse join(JoinRequest joinRequest) {
        validateDuplicatedEmail(joinRequest.getEmail());
        Role role = Role.find(joinRequest.getRole());

        User user = User.create(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getName(),
                role, joinRequest.getPhoneNumber());

        if (role == Role.MEMBER) {
            validateRequiredData(joinRequest);
            User trainer = validateUserCode(joinRequest.getUserCode());
            userRepository.save(user);
            rabbitMqService.publishCreateNewMember(new CreateNewMemberPubDto(user.getEmail(), trainer.getEmail(), joinRequest.getCenterName(), joinRequest.getRegistrationCount()));
            return new JoinResponse(user.getEmail(), user.getName(), user.getRole().getName(), trainer.getName(), joinRequest.getCenterName(), joinRequest.getRegistrationCount());
        } else {
            userRepository.save(user);
            return new JoinResponse(user.getEmail(), user.getName(), user.getRole().getName(), null, null, null);
        }
    }

    private void validateDuplicatedEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void validateRequiredData(JoinRequest joinRequest) {
        if (joinRequest.getUserCode() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_USER_CODE);
        }
        if (joinRequest.getCenterName() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_CENTER_NAME);
        }
        if (joinRequest.getRegistrationCount() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_REGISTRATION_COUNT);
        }
    }

    private User validateUserCode(String userCode) {
        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_USER_CODE));
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
