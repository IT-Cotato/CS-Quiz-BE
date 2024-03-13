package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;

public record QuizResultInfo(
        Long quizId,
        int quizNumber,
        Long scorerId,
        String scorerName,
        String backFourNumber
) {
    public static QuizResultInfo from(Quiz quiz, Member member) {
        return new QuizResultInfo(
                quiz.getId(),
                quiz.getNumber(),
                member.getId(),
                member.getName(),
                member.getBackFourNumber()
        );
    }

    public static QuizResultInfo noScorer(Quiz quiz) {
        return new QuizResultInfo(
                quiz.getId(),
                quiz.getNumber(),
                null,
                null,
                null
        );
    }
}
