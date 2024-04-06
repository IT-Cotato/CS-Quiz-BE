package cotato.csquiz.controller.dto.session;

import jakarta.validation.constraints.NotNull;

public record SessionNumRequest(
        @NotNull
        Long sessionId,
        @NotNull
        Integer sessionNum
) {
}
