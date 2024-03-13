package cotato.csquiz.controller.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class AddAdditionalAnswerRequest {
    private Long quizId;
    private String answer;
}
