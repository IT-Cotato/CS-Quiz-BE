package cotato.csquiz.domain.dto.mypage;

import cotato.csquiz.domain.entity.Member;
import lombok.Lombok;

public record HallOfFameInfo(
        Long memberId,
        String name,
        long count
) {
    public static HallOfFameInfo from(Member member, long count) {
        return new HallOfFameInfo(
                member.getId(),
                member.getName(),
                count
        );
    }
}

