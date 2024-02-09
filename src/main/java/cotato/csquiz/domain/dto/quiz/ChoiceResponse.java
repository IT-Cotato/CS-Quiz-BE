package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.enums.ChoiceCorrect;

public record ChoiceResponse(
        Long choiceId,
        int number,
        String content,
        ChoiceCorrect isAnswer
) {
    public static ChoiceResponse from(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                choice.getChoiceNumber(),
                choice.getContent(),
                choice.getIsCorrect()
        );
    }
}
