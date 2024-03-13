package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.enums.MemberPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberApproveRequest {

    private Long memberId;
    private MemberPosition position;
    private Long generationId;
}
