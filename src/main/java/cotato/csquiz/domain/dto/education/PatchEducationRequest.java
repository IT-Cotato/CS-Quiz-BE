package cotato.csquiz.domain.dto.education;

import lombok.Data;

@Data
public class PatchEducationRequest {
    private long educationId;
    private int status;
}
