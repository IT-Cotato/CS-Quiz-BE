package cotato.csquiz.controller.dto.socket;

import jakarta.validation.constraints.NotNull;

public record QuizCloseRequest(
        @NotNull
        Long educationId
) {
}
