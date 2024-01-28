package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.ChoiceCorrect;
import lombok.Data;

@Data
public class CreateChoiceRequest {

    private String content;
    private int number;
    private ChoiceCorrect isAnswer;
}
