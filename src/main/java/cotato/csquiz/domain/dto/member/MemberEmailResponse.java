package cotato.csquiz.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberEmailResponse {

    private String email;
}
