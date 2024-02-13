package cotato.csquiz.domain.dto.mypage;

import cotato.csquiz.domain.entity.Member;

public record MyHallOfFameInfo(
        Long memberId,
        long scorerCount,
        long answerCount
) {
    public static MyHallOfFameInfo from(Member member, long scorerCount, long answerCount) {
        return new MyHallOfFameInfo(
                member.getId(),
                scorerCount,
                answerCount
        );
    }
}
