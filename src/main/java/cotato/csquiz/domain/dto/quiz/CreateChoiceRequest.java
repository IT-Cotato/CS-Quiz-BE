package cotato.csquiz.domain.dto.quiz;

import lombok.Data;

@Data
public class CreateChoiceRequest {

    private String content;
    private int number;
    private boolean isAnswer;
}
