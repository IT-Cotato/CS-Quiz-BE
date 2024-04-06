package cotato.csquiz.controller.dto.record;

public record RegradeRequest(
        Long quizId,
        String newAnswer
) {
}
