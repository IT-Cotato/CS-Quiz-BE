package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.entity.EducationStatus;
import lombok.Getter;

@Getter
public class PatchStatusRequest {
    private long educationId;
    private EducationStatus status;
}
