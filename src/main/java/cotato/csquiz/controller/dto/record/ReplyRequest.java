package cotato.csquiz.controller.dto.record;

public record ReplyRequest(
        Long quizId,
        Long memberId,
        String input
) {
}
