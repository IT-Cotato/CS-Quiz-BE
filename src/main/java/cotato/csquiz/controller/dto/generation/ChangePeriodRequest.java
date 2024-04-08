package cotato.csquiz.controller.dto.generation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ChangePeriodRequest(
        @NotNull
        Long generationId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}
