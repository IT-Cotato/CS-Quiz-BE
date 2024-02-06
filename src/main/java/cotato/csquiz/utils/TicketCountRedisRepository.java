package cotato.csquiz.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketCountRedisRepository {

    private static final String KEY_PREFIX = "$ticket for ";
    private final RedisTemplate<String, Object> redisTemplate;

    public Long increment(Long quizId) {
        String key = KEY_PREFIX + quizId;
        return redisTemplate.opsForValue()
                .increment(key);
    }
}
