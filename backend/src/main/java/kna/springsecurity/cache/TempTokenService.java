package kna.springsecurity.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TempTokenService {

    private static final Logger log = LoggerFactory.getLogger(TempTokenService.class);

    public enum TempTokenPurpose {
        LOGIN_2FA,
        REGISTER_2FA_SETUP
    }

    public record TempTokenData(Long userId, TempTokenPurpose purpose) {}

    private final StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final String PREFIX = "2fa:token:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final Map<String, InMemoryTokenData> inMemoryTokens = new ConcurrentHashMap<>();

    private record InMemoryTokenData(TempTokenData data, Instant expiresAt) {}

    public String generateAndStore(Long userId) {
        return generateAndStore(userId, TempTokenPurpose.LOGIN_2FA);
    }

    public String generateAndStore(Long userId, TempTokenPurpose purpose) {
        String token = generateToken();

        try {
            redisTemplate.opsForValue().set(
                PREFIX + token,
                purpose.name() + ":" + userId,
                TTL
            );
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable. Falling back to in-memory temp token storage.", ex);
            inMemoryTokens.put(
                token,
                new InMemoryTokenData(
                    new TempTokenData(userId, purpose),
                    Instant.now().plus(TTL)
                )
            );
        }

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
        String value;
        try {
            value = redisTemplate.opsForValue().get(PREFIX + tempToken);
        } catch (RedisConnectionFailureException ex) {
            return getTokenDataFromMemory(tempToken, ex);
        }

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
        try {
            redisTemplate.delete(PREFIX + token);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis unavailable while deleting temp token. Cleaning up in-memory token.", ex);
            inMemoryTokens.remove(token);
        }

        inMemoryTokens.remove(token);
    }

    private TempTokenData getTokenDataFromMemory(String tempToken, RedisConnectionFailureException ex) {
        log.warn("Redis unavailable while reading temp token. Trying in-memory token storage.", ex);
        InMemoryTokenData fallbackData = inMemoryTokens.get(tempToken);
        if (fallbackData == null) {
            return null;
        }

        if (Instant.now().isAfter(fallbackData.expiresAt())) {
            inMemoryTokens.remove(tempToken);
            return null;
        }

        return fallbackData.data();
    }
}
