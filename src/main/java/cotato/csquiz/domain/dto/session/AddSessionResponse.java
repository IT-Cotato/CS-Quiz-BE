package cotato.csquiz.domain.dto.session;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddSessionResponse {
    private long sessionId;
    private int sessionNumber;
}
