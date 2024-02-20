package cotato.csquiz.domain.dto.auth;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberRole;

public record MemberInfoResponse(
        Long memberId,
        String memberName,
        String backFourNumber,
        MemberRole role
) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                member.getBackFourNumber(),
                member.getRole()
        );
    }
}
