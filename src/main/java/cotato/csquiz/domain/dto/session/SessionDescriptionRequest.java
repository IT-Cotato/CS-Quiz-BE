package cotato.csquiz.domain.dto.session;

import lombok.Getter;

@Getter
public class SessionDescriptionRequest {
    private long sessionId;
    private String description;
}
