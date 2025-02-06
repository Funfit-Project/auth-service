package funfit.auth.api;

import funfit.auth.entity.User;
import funfit.auth.kafka.dto.DeductAndCompensatePoints;
import funfit.auth.user.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.api.dto.ResponseUserDtoForCommunity;
import funfit.auth.api.dto.ResponseUserDtoForPt;
import funfit.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignClientController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/feignClient/user/pt")
    public ResponseUserDtoForPt requestUserByEmailForPt(@RequestParam String email) {
        log.info("Feign Client | request url = /feignClient/user/pt");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        return new ResponseUserDtoForPt(user.getId(), user.getEmail(), user.getName(), user.getRole().getName(),
                user.getPhoneNumber(), user.getUserCode());
    }

    @GetMapping("/feignClient/user/community")
    public ResponseUserDtoForCommunity requestUserByEmailForCommunity(@RequestParam String email) {
        log.info("Feign Client | request url = /feignClient/user/community");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        return new ResponseUserDtoForCommunity(user.getId(), user.getEmail(), user.getName(), user.getRole().getName());
    }

    @PostMapping("/deduct/points")
    public boolean deductPoints(@RequestBody DeductAndCompensatePoints deductAndCompensatePoints) {
        log.info("Feign Client | request url = /deduct/points");
        return userService.deductPoints(deductAndCompensatePoints);
    }
}
