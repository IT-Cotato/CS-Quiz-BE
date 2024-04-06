package cotato.csquiz.controller.dto.session;

import jakarta.validation.constraints.NotNull;

public record SessionDescriptionRequest(
        @NotNull
        Long sessionId,
        @NotNull
        String description
) {
}
