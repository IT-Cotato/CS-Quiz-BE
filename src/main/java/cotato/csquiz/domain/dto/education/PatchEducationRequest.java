package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.entity.EducationStatus;
import lombok.Data;

@Data
public class PatchEducationRequest {
    private long educationId;
    private EducationStatus status;
}
