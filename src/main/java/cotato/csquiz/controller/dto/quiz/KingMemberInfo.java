package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.Member;

public record KingMemberInfo(
        Long memberId,
        String memberName,
        String backFourNumber
) {
    public static KingMemberInfo from(Member member) {
        return new KingMemberInfo(
                member.getId(),
                member.getName(),
                member.getBackFourNumber()
        );
    }
}
