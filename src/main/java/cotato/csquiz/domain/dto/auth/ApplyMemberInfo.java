package cotato.csquiz.domain.dto.auth;

import cotato.csquiz.domain.entity.Member;
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

    public static ApplyMemberInfo from(Member member, String backFourNumber) {
        return new ApplyMemberInfo(
                member.getId(),
                member.getName(),
                backFourNumber
        );
    }
}
