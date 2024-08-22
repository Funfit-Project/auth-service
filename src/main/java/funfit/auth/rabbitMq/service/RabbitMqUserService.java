package funfit.auth.rabbitMq.service;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.api.dto.ResponseUserDtoForCommunity;
import funfit.auth.api.dto.ResponseUserDtoForPt;
import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RabbitMqUserService {

    private final UserRepository userRepository;

    public ResponseUserDtoForPt requestUserInfoFotPt(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        return new ResponseUserDtoForPt(user.getId(), user.getEmail(), user.getName(), user.getRole().getName(), user.getPhoneNumber(), user.getUserCode());
    }

    public ResponseUserDtoForCommunity requestUserInfoFotCommunity(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        return new ResponseUserDtoForCommunity(user.getId(), user.getEmail(), user.getName(), user.getRole().getName());
    }
}
