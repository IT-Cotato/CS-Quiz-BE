package cotato.csquiz.domain.dto.session;

import lombok.Getter;

@Getter
public class SessionPhotoUrlRequest {
    private long sessionId;
    private String photoUrl;
}
