package cotato.csquiz.domain.dto.session;

import lombok.Getter;

@Getter
public class AddSessionRequest {
    private int sessionNum;
    private long generationId;
    private String photoURL;
    private String description;
}
