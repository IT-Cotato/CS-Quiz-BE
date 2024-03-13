package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.domain.enums.MemberPosition;

public record WinnerInfoResponse(
        Long memberId,
        String memberName,
        Long educationNumber,
        String backFourNumber,
        MemberPosition memberPosition
) {
    public static WinnerInfoResponse from(Winner winner) {
        return new WinnerInfoResponse(
                winner.getMember().getId(),
                winner.getMember().getName(),
                winner.getEducation().getId(),
                winner.getMember().getBackFourNumber(),
                winner.getMember().getPosition()
        );
    }
}
