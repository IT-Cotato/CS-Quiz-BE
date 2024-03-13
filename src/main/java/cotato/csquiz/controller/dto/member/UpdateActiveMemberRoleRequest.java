package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateActiveMemberRoleRequest {

    private Long memberId;
    private MemberRole role;
}
