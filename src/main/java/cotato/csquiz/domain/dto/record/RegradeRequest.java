package cotato.csquiz.domain.dto.record;

public record RegradeRequest(
        Long quizId,
        String newAnswer
) {
}
