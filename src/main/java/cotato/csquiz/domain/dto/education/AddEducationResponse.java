package cotato.csquiz.domain.dto.education;

import lombok.Builder;
import lombok.Data;

import java.util.Locale;

@Data
@Builder
public class AddEducationResponse {

    private Long educationId;
}
