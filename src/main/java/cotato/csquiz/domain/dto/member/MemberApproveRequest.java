package cotato.csquiz.domain.dto.member;

import cotato.csquiz.domain.entity.MemberPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberApproveRequest {

    private Long userId;
    private MemberPosition position;
    private Long generationId;
}
