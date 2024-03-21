package cotato.csquiz.domain.dto.socket;

public record QuizStopResponse(
        String command,
        Long quizId
) {
    public static QuizStopResponse from(String command, Long quizId) {
        return new QuizStopResponse(
                command,
                quizId
        );
    }
}
