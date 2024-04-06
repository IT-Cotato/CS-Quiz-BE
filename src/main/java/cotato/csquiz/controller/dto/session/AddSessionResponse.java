package cotato.csquiz.controller.dto.session;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddSessionResponse {
    private Long sessionId;
    private int sessionNumber;
}
