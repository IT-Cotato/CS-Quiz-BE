package cotato.csquiz.domain.dto.member;

import cotato.csquiz.domain.enums.MemberRole;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateActiveMemberRoleRequest {

    private List<Long> memberIds;
    private MemberRole role;
}
