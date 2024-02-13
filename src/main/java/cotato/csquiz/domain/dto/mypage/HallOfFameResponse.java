package cotato.csquiz.domain.dto.mypage;

import java.util.List;

public record HallOfFameResponse(
        List<HallOfFameInfo> scorerInfo,
        List<HallOfFameInfo> answerInfo,
        MyHallOfFameInfo myInfo
) {
    public static HallOfFameResponse from(
            List<HallOfFameInfo> scorerInfo, List<HallOfFameInfo> answerInfo, MyHallOfFameInfo myInfo) {
        return new HallOfFameResponse(
                scorerInfo,
                answerInfo,
                myInfo
        );
    }
}
