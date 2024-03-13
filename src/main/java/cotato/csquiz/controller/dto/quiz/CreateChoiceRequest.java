package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.enums.ChoiceCorrect;
import lombok.Data;

@Data
public class CreateChoiceRequest {

    private String content;
    private int number;
    private ChoiceCorrect isAnswer;
}
