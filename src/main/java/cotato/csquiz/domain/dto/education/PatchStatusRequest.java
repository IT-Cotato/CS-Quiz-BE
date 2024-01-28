package cotato.csquiz.domain.dto.education;

import lombok.Getter;

@Getter
public class PatchStatusRequest {
    private long educationId;
    private boolean status;
}
