package funfit.auth.auth;

import funfit.auth.exception.ErrorCode;
import funfit.auth.exception.customException.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_KEY_PREFIX ="refresh_token:";

    public void saveRefreshToken(String newRefreshToken, String oldRefreshToken) {
        if (oldRefreshToken != null && redisTemplate.hasKey(REFRESH_TOKEN_KEY_PREFIX + oldRefreshToken)) {
            redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + oldRefreshToken);
        }
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + newRefreshToken, newRefreshToken, Duration.ofDays(30));
    }

    public void validateRefreshToken(String refreshToken) {
        if (!redisTemplate.opsForSet().isMember(REFRESH_TOKEN_KEY_PREFIX, refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public void logout(String refreshToken) {
        validateRefreshToken(refreshToken);
        if (redisTemplate.hasKey(REFRESH_TOKEN_KEY_PREFIX + refreshToken)) {
            redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + refreshToken);
        }
    }
}
