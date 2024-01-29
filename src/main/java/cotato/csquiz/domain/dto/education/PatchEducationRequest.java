package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.enums.EducationStatus;
import lombok.Data;

@Data
public class PatchEducationRequest {
    private long educationId;
    private EducationStatus status;
}
