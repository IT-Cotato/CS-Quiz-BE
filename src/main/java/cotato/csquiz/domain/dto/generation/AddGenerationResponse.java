package cotato.csquiz.domain.dto.generation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddGenerationResponse {
    private long generationId;
}
