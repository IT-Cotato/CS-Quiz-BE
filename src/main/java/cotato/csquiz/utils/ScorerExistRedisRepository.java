package cotato.csquiz.utils;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.repository.QuizRepository;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScorerExistRedisRepository {

    private static final String KEY_PREFIX = "$Scorer for ";
    private static final Integer SCORER_EXPIRATION = 120;
    private final QuizRepository quizRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveAllScorerNone(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);
        quizzes.forEach(this::saveScorerNone);
    }

    private void saveScorerNone(Quiz quiz) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForValue().set(
                quizKey,
                false,
                SCORER_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    public void saveScorer(Quiz quiz) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForValue().set(quizKey, true);
    }

    public boolean isExist(Quiz quiz) {
        String quizKey = KEY_PREFIX + quiz.getId();
        return Objects.equals(redisTemplate.opsForValue().get(quizKey), true);
    }
}
