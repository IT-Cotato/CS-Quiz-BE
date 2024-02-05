package cotato.csquiz.domain.dto.session;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SessionPhotoUrlRequest {

    private Long sessionId;
    private MultipartFile sessionImage;
}
