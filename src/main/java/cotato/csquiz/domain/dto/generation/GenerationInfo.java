package cotato.csquiz.domain.dto.generation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerationInfo {

    private Long generationId;
    private String generationName;
}
