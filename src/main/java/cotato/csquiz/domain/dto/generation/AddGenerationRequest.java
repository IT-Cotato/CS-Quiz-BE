package cotato.csquiz.domain.dto.generation;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class AddGenerationRequest {
    private int generationNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private int sessionCount;
}
