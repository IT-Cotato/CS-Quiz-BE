package cotato.csquiz.controller.dto.record;

import cotato.csquiz.domain.entity.Scorer;

public record ScorerResponse(
        Long scorerId,
        Long memberId,
        String memberName,
        String backFourNumber
) {
    public static ScorerResponse from(Scorer scorer, String backFourNumber) {
        return new ScorerResponse(
                scorer.getId(),
                scorer.getMember().getId(),
                scorer.getMember().getName(),
                backFourNumber
        );
    }
}
