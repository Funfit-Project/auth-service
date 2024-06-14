package funfit.auth.api;

import funfit.auth.auth.entity.User;
import funfit.auth.auth.repository.UserRepository;
import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import funfit.auth.api.dto.ResponseUserDtoForCommunity;
import funfit.auth.api.dto.ResponseUserDtoForPt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignClientController {

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
}
