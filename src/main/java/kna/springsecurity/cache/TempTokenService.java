package kna.springsecurity.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TempTokenService {

    public enum TempTokenPurpose {
        LOGIN_2FA,
        REGISTER_2FA_SETUP
    }

    public record TempTokenData(Long userId, TempTokenPurpose purpose) {}

    private final StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final String PREFIX = "2fa:token:";
    private static final Duration TTL = Duration.ofMinutes(5);

    public String generateAndStore(Long userId) {
        return generateAndStore(userId, TempTokenPurpose.LOGIN_2FA);
    }

    public String generateAndStore(Long userId, TempTokenPurpose purpose) {
        String token = generateToken();

        redisTemplate.opsForValue().set(
                PREFIX + token,
                purpose.name() + ":" + userId,
                TTL
        );

        return token;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.encodeBase64URLSafeString(bytes);
    }

    public Long getUserId(String tempToken) {
        TempTokenData data = getTokenData(tempToken);
        return data != null ? data.userId() : null;
    }

    public TempTokenData getTokenData(String tempToken) {
        String value = redisTemplate.opsForValue().get(PREFIX + tempToken);
        if (!StringUtils.hasText(value)) {
            return null;
        }

        // Backward compatibility with older tokens that only stored userId.
        if (!value.contains(":")) {
            return new TempTokenData(Long.valueOf(value), TempTokenPurpose.LOGIN_2FA);
        }

        String[] parts = value.split(":", 2);
        if (parts.length != 2) {
            return null;
        }

        try {
            TempTokenPurpose purpose = TempTokenPurpose.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            return new TempTokenData(userId, purpose);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
