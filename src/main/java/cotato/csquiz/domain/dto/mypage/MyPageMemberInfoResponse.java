package cotato.csquiz.domain.dto.mypage;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;
import cotato.csquiz.domain.enums.MemberRole;

public record MyPageMemberInfoResponse(
        Long memberId,
        String memberName,
        String phoneNumber,
        int generationNumber,
        MemberRole memberRole,
        MemberPosition memberPosition
) {
    public static MyPageMemberInfoResponse from(Member member, String originPhoneNumber) {
        return new MyPageMemberInfoResponse(
                member.getId(),
                member.getName(),
                originPhoneNumber,
                member.getGeneration().getNumber(),
                member.getRole(),
                member.getPosition()
        );
    }
}
