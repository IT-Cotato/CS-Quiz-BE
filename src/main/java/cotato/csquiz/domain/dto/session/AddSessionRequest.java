package cotato.csquiz.domain.dto.session;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddSessionRequest {
    private int sessionNum;
    private long generationId;
    private MultipartFile sessionImage;
    private String description;
}
