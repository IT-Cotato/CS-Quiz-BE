package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.QuizStatus;
import lombok.Builder;

@Builder
public record CsAdminQuizResponse(
        Long quizId,
        String question,
        Integer quizNumber,
        QuizStatus status,
        QuizStatus start
){
    public static CsAdminQuizResponse from(Quiz quiz) {
        return new CsAdminQuizResponse(
                quiz.getId(),
                quiz.getQuestion(),
                quiz.getNumber(),
                quiz.getStatus(),
                quiz.getStart()
        );
    }
}
