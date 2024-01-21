package cotato.csquiz.domain.dto.education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EducationResponse {

    private final Long educationId;
    private final Long generationId;
    private final Integer educationNumber;
}
