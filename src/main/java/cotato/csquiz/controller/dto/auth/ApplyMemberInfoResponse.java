package cotato.csquiz.controller.dto.auth;

import cotato.csquiz.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyMemberInfoResponse {

    private Long id;
    private String name;
    private String backFourNumber;

    public static ApplyMemberInfoResponse from(Member member, String backFourNumber) {
        return new ApplyMemberInfoResponse(
                member.getId(),
                member.getName(),
                backFourNumber
        );
    }
}
