package cotato.csquiz.utils;

import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.entity.MultipleQuiz;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.ShortAnswer;
import cotato.csquiz.domain.entity.ShortQuiz;
import cotato.csquiz.domain.enums.ChoiceCorrect;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.ChoiceRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ShortAnswerRepository;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QuizAnswerRedisRepository {

    private static final String KEY_PREFIX = "$quiz";
    private static final Integer QUIZ_ANSWER_EXPIRATION_TIME = 120;
    private final QuizRepository quizRepository;
    private final ShortAnswerRepository shortAnswerRepository;
    private final ChoiceRepository choiceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveAllQuizAnswers(Long educationId) {
        List<Quiz> allQuizzes = quizRepository.findAllByEducationId(educationId);
        allQuizzes.forEach(this::saveQuizAnswer);
    }

    public void saveAdditionalQuizAnswer(Quiz quiz, String answer) {
        if (quiz instanceof ShortQuiz) {
            ShortAnswer shortAnswer = shortAnswerRepository.findByShortQuizAndContent((ShortQuiz) quiz, answer);
            saveAdditionalShortQuizAnswer(quiz, shortAnswer);
        }
        if (quiz instanceof MultipleQuiz) {
            Choice choice = choiceRepository.findByMultipleQuizAndChoiceNumber((MultipleQuiz) quiz,
                            Integer.parseInt(answer))
                    .orElseThrow(() -> new AppException(ErrorCode.ANSWER_VALIDATION_FAULT));
            saveAdditionalMultipleQuizAnswer(quiz, choice.getChoiceNumber());
        }
    }

    private void saveAdditionalMultipleQuizAnswer(Quiz quiz, int answerNumber) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForValue().set(
                quizKey,
                answerNumber,
                QUIZ_ANSWER_EXPIRATION_TIME,
                TimeUnit.MINUTES
        );
    }

    private void saveAdditionalShortQuizAnswer(Quiz quiz, ShortAnswer answer) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForList()
                .rightPush(quizKey, answer);
        redisTemplate.expire(quizKey, QUIZ_ANSWER_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    private void saveQuizAnswer(Quiz quiz) {
        if (quiz instanceof ShortQuiz) {
            saveShortAnswer(quiz);
        }
        if (quiz instanceof MultipleQuiz) {
            saveChoices(quiz);
        }
    }

    private void saveShortAnswer(Quiz quiz) {
        List<ShortAnswer> shortAnswers = shortAnswerRepository.findAllByShortQuiz((ShortQuiz) quiz);
        List<String> answers = shortAnswers.stream()
                .map(ShortAnswer::getContent)
                .toList();
        String quizKey = KEY_PREFIX + quiz.getId();
        for (String answer : answers) {
            redisTemplate.opsForList()
                    .rightPush(quizKey, answer);
        }
        redisTemplate.expire(quizKey, QUIZ_ANSWER_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    private void saveChoices(Quiz quiz) {
        List<Choice> choices = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) quiz);
        int answerNumber = choices.stream()
                .filter(choice -> choice.getIsCorrect() == ChoiceCorrect.ANSWER)
                .mapToInt(Choice::getChoiceNumber)
                .findAny()
                .orElse(0);
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForValue().set(
                quizKey,
                answerNumber,
                QUIZ_ANSWER_EXPIRATION_TIME,
                TimeUnit.MINUTES
        );
    }

    public boolean isCorrect(final Quiz quiz, final String input) {
        String quizKey = KEY_PREFIX + quiz.getId();
        if (quiz instanceof ShortQuiz) {
            return hasShortAnswers(input, quizKey);
        }
        if (quiz instanceof MultipleQuiz) {
            return isCorrectChoices(input, quizKey);
        }
        return false;
    }

    private boolean isCorrectChoices(String input, String quizKey) {
        Object answer = redisTemplate.opsForValue().get(quizKey);
        log.info("찾은 퀴즈 정답 : {}", answer);
        return Objects.equals(answer, Integer.parseInt(input));
    }

    private boolean hasShortAnswers(String input, String quizKey) {
        List<Object> range = redisTemplate.opsForList().range(quizKey, 0, -1);
        assert range != null;
        return range.contains(input);
    }
}
