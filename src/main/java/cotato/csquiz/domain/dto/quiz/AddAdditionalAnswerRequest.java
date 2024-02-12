package cotato.csquiz.domain.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AddAdditionalAnswerRequest {
    private Long quizId;
    private String answer;
}
