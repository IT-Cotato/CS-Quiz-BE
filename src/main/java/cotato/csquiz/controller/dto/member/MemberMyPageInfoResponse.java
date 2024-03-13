package cotato.csquiz.controller.dto.member;

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
    public static MemberMyPageInfoResponse from(Member member) {
        return new MemberMyPageInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getGeneration().getNumber(),
                member.getPosition(),
                member.getPhoneNumber()
        );
    }
}
