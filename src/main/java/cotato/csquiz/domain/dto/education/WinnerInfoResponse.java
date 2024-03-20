package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.domain.enums.MemberPosition;

public record WinnerInfoResponse(
        Long memberId,
        String memberName,
        Long educationNumber,
        String backFourNumber,
        MemberPosition memberPosition
) {
    public static WinnerInfoResponse from(Winner winner, String backFourNumber) {
        return new WinnerInfoResponse(
                winner.getMember().getId(),
                winner.getMember().getName(),
                winner.getEducation().getId(),
                backFourNumber,
                winner.getMember().getPosition()
        );
    }
}
