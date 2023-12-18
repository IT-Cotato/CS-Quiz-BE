package cotato.csquiz.domain.dto.session;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SessionPhotoUrlRequest {
    private long sessionId;
    private MultipartFile sessionImage;
}
