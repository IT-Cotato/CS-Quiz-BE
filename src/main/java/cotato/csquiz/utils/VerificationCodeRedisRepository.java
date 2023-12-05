package cotato.csquiz.utils;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VerificationCodeRedisRepository {

    private static final Integer VERIFICATION_CODE_EXPIRATION_TIME = 10;
    private static final String KEY_PREFIX = "$email$";
    private final RedisTemplate<String, String> redisTemplate;

    public String getByEmail(String email){
        String queryKey = KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(queryKey);
    }

    public void saveCodeWithEmail(String email, String verificationCode){

        String saveKey = KEY_PREFIX + email;
        redisTemplate.opsForValue().set(
                saveKey,
                verificationCode,
                VERIFICATION_CODE_EXPIRATION_TIME,
                TimeUnit.MINUTES
        );
    }
}
