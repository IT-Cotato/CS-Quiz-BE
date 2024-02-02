package cotato.csquiz.domain.dto.auth;

import cotato.csquiz.domain.entity.MemberPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveMemberInfoResponse {

    private Long id;
    private String name;
    private MemberPosition position;
    private String generationName;
}
