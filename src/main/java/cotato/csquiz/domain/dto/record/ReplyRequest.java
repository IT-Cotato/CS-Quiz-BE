package cotato.csquiz.domain.dto.record;

public record ReplyRequest(
        Long quizId,
        Long memberId,
        String input
) {
}
