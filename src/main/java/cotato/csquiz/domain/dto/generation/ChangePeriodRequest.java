package cotato.csquiz.domain.dto.generation;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ChangePeriodRequest {

    private Long generationId;
    private LocalDate startDate;
    private LocalDate endDate;
}
