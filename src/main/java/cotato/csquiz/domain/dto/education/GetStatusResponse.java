package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.entity.EducationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetStatusResponse {
    private EducationStatus status;
}
