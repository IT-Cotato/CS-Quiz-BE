package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;
import cotato.csquiz.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEnrollInfoResponse {

    private Long memberId;
    private String memberName;
    private MemberPosition position;
    private int generationNumber;
    private MemberRole role;

    public static MemberEnrollInfoResponse from(Member member) {
        return new MemberEnrollInfoResponse(
                member.getId(),
                member.getName(),
                member.getPosition(),
                member.getGeneration().getNumber(),
                member.getRole()
        );
    }
}
