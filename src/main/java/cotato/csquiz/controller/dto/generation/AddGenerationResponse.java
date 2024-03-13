package cotato.csquiz.controller.dto.generation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddGenerationResponse {

    private Long generationId;
}
