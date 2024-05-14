package funfit.auth.rabbitMq.controller;

import funfit.auth.rabbitMq.dto.ResponseUserDtoForCommunity;
import funfit.auth.rabbitMq.dto.ResponseUserDtoForPt;
import funfit.auth.rabbitMq.service.RabbitMqUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitMqController {

    private final RabbitMqUserService rabbitMqUserService;

    @GetMapping("/userInfo/pt/{userId}")
    public ResponseUserDtoForPt requestUserInfoFotPt(@PathVariable long userId) {
        return rabbitMqUserService.requestUserInfoFotPt(userId);
    }

    @GetMapping("/userInfo/community/{userId}")
    public ResponseUserDtoForCommunity requestUserInfoFotCommunity(@PathVariable long userId) {
        return rabbitMqUserService.requestUserInfoFotCommunity(userId);
    }
}
