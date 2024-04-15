package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.enums.EducationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindStatusResponse {

    private EducationStatus status;
}
