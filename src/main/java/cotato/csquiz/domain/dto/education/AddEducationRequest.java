package cotato.csquiz.domain.dto.education;

import lombok.Data;

@Data
public class AddEducationRequest {

    private String subject;
    private Long sessionId;
    private int educationNum;

}
