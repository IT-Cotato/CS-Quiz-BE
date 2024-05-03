package cotato.csquiz.controller.dto.auth;

import jakarta.validation.constraints.NotNull;

public record FindPasswordResponse (
        @NotNull
        String accessToken
) {
}
