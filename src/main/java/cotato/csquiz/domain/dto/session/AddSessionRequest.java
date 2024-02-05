package cotato.csquiz.domain.dto.session;

import cotato.csquiz.domain.entity.CSEducation;
import cotato.csquiz.domain.entity.ItIssue;
import cotato.csquiz.domain.entity.Networking;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddSessionRequest {

    private Long generationId;
    private MultipartFile sessionImage;
    private String description;
    private ItIssue itIssue;
    private Networking networking;
    private CSEducation csEducation;
}
