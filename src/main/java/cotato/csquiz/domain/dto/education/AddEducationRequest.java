package cotato.csquiz.domain.dto.education;

import lombok.Data;

@Data
public class AddEducationRequest {
    private String subject;
    private long sessionId;
    private int educationNum;

}
