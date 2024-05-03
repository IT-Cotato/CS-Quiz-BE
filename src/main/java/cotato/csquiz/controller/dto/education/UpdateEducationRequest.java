package cotato.csquiz.controller.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateEducationRequest(
        @NotNull
        Long educationId,
        @NotBlank
        String newSubject,
        @NotNull
        Integer newNumber
) {
}
