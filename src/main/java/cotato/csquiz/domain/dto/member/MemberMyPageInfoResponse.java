package cotato.csquiz.domain.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;

public record MemberMyPageInfoResponse(
        Long memberId,
        String email,
        String name,
        int generationNumber,
        MemberPosition memberPosition,
        String phoneNumber
) {
    public static MemberMyPageInfoResponse from(Member member, String originPhoneNumber) {
        return new MemberMyPageInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getGeneration().getNumber(),
                member.getPosition(),
                originPhoneNumber
        );
    }
}
