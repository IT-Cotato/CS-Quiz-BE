package cotato.csquiz.domain.dto.session;

import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.domain.enums.ItIssue;
import cotato.csquiz.domain.enums.Networking;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateSessionRequest {

    private Long sessionId;
    private MultipartFile sessionImage;
    private Boolean isPhotoUpdated;
    private String description;
    private ItIssue itIssue;
    private Networking networking;
    private CSEducation csEducation;
}
