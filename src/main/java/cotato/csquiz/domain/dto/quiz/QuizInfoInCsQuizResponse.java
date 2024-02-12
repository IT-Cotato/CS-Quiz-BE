package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import java.util.List;

public record QuizInfoInCsQuizResponse(
        Long quizId,
        Integer quizNumber,
        String question,
        List<String> answer
) {
    public static QuizInfoInCsQuizResponse from(Quiz quiz, List<String> answer) {
        return new QuizInfoInCsQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                answer
        );
    }
}
