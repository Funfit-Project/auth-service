package funfit.auth.user.service;

import funfit.auth.entity.User;
import funfit.auth.outbox.Outbox;
import funfit.auth.outbox.OutboxRepository;
import funfit.auth.user.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.user.dto.EditUserInfoRequest;
import funfit.auth.user.dto.ReadUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;

    public ReadUserResponse readUserInfo(String email) {
        User user = findUser(email);
        return new ReadUserResponse(user);
    }

    public ReadUserResponse editUserInfo(EditUserInfoRequest dto, String email) {
        User user = findUser(email);
        user.editUserInfo(dto.getName());

        Outbox outbox = Outbox.create(user.getEmail(), "user-info-updated");
        outboxRepository.save(outbox);

        return new ReadUserResponse(user);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EMAIL));
    }
}
