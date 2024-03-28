package cotato.csquiz.domain.dto.quiz;

import lombok.Data;

@Data
public class AddAdditionalAnswerRequest {
    private Long quizId;
    private String answer;
}
