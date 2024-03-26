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
import java.util.Set;
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
    private static final Integer QUIZ_ANSWER_EXPIRATION_TIME = 60 * 24;
    private final QuizRepository quizRepository;
    private final ShortAnswerRepository shortAnswerRepository;
    private final ChoiceRepository choiceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

//    public static void main(String[] args) {
//        List<Integer> answers = List.of(1, 2, 3, 4);
//        List<Object> objects = List.of(1, 2, 4, 3);
//        System.out.println(answers.equals(objects));
//    }

    public void saveAllQuizAnswers(Long educationId) {
        List<Quiz> allQuizzes = quizRepository.findAllByEducationId(educationId);
        allQuizzes.forEach(this::saveQuizAnswer);
    }

    public void saveAdditionalQuizAnswer(Quiz quiz, String answer) {
        if (quiz instanceof ShortQuiz) {
            ShortAnswer shortAnswer = shortAnswerRepository.findByShortQuizAndContent((ShortQuiz) quiz, answer)
                    .orElseThrow(() -> new AppException(ErrorCode.CONTENT_IS_NOT_ANSWER));
            saveAdditionalShortQuizAnswer(quiz, shortAnswer.getContent());
        }
        if (quiz instanceof MultipleQuiz) {
            Choice choice = choiceRepository.findByMultipleQuizAndChoiceNumber((MultipleQuiz) quiz,
                            Integer.parseInt(answer))
                    .orElseThrow(() -> new AppException(ErrorCode.ANSWER_VALIDATION_FAULT));
            saveAdditionalMultipleQuizAnswer(quiz, choice.getChoiceNumber());
        }
    }

    private void saveAdditionalMultipleQuizAnswer(Quiz quiz, Integer answerNumber) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForList().rightPush(quizKey, answerNumber);
        redisTemplate.expire(quizKey, QUIZ_ANSWER_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    private void saveAdditionalShortQuizAnswer(Quiz quiz, String answer) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForList().rightPush(quizKey, answer);
        redisTemplate.expire(quizKey, QUIZ_ANSWER_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    public void saveQuizAnswer(Quiz quiz) {
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
        List<Integer> answerNumbers = choices.stream().filter(choice -> choice.getIsCorrect() == ChoiceCorrect.ANSWER)
                .map(Choice::getChoiceNumber).toList();

        String quizKey = KEY_PREFIX + quiz.getId();
        for (Integer answerNumber : answerNumbers) {
            redisTemplate.opsForSet().add(quizKey, answerNumber);
        }
        redisTemplate.expire(quizKey, QUIZ_ANSWER_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    public boolean isCorrect(final Quiz quiz, final List<String> inputs) {
        String quizKey = KEY_PREFIX + quiz.getId();
        if (quiz instanceof ShortQuiz) {
            return hasShortAnswers(inputs.get(0), quizKey);
        }
        if (quiz instanceof MultipleQuiz) {
            return isCorrectChoices(inputs, quizKey);
        }
        return false;
    }

    private boolean isCorrectChoices(List<String> inputs, String quizKey) {
        Set<Object> redis = redisTemplate.opsForSet().members(quizKey);
        Objects.requireNonNull(redis);
        log.info("[입력한 정답]: {}", inputs);
        List<Integer> enrolledAnswer = redis.stream()
                .map(object -> (Integer) object)
                .sorted()
                .toList();
        log.info("[등록된 정답]: {}", enrolledAnswer);
        List<Integer> answers = inputs.stream()
                .map(Integer::parseInt)
                .sorted()
                .toList();
        log.info("[입력한 정수 정답]: {}", answers);
        return enrolledAnswer.equals(answers);
    }

    private boolean hasShortAnswers(String input, String quizKey) {
        List<Object> range = redisTemplate.opsForList().range(quizKey, 0, -1);
        Objects.requireNonNull(range);
        return range.contains(input);
    }
}
