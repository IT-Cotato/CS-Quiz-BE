package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.QuizType;
import java.util.List;

public record QuizInfoInCsQuizResponse(
        Long quizId,
        QuizType quizType,
        int quizNumber,
        String question,
        List<String> answer
) {
    public static QuizInfoInCsQuizResponse from(Quiz quiz, List<String> answer) {
        return new QuizInfoInCsQuizResponse(
                quiz.getId(),
                quiz.getQuizType(),
                quiz.getNumber(),
                quiz.getQuestion(),
                answer
        );
    }
}
