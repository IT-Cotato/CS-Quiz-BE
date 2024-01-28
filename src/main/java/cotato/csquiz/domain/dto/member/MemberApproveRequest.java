package cotato.csquiz.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberApproveRequest {

    Long userId;
    String position;
    String generationName;
}
