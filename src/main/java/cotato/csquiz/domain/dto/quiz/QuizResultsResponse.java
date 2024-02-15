package cotato.csquiz.domain.dto.quiz;

import java.util.List;

public record QuizResultsResponse(
        List<QuizResultInfo> resultInfos
) {
    public static QuizResultsResponse of(List<QuizResultInfo> quizResultInfos) {
        return new QuizResultsResponse(
                quizResultInfos
        );
    }
}
