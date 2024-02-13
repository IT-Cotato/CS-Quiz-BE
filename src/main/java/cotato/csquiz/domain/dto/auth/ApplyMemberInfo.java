package cotato.csquiz.domain.dto.auth;

import cotato.csquiz.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyMemberInfo {

    private Long id;
    private String name;
    private String backFourNumber;
}
