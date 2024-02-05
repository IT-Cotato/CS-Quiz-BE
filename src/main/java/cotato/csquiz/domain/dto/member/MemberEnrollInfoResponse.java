package cotato.csquiz.domain.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MemberPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEnrollInfoResponse {

    private Long id;
    private String name;
    private MemberPosition position;
    private String generationName;

    public static MemberEnrollInfoResponse from(Member member) {
        return new MemberEnrollInfoResponse(
                member.getId(),
                member.getName(),
                member.getPosition(),
                member.getGeneration().getName()
        );
    }
}
