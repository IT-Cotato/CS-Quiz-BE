package cotato.csquiz.controller.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindPasswordResponse {

    private String accessToken;
}
