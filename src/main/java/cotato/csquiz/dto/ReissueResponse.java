package cotato.csquiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReissueResponse {

    private String accessToken;
}
