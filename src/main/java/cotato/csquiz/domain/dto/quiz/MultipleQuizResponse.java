package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleQuizResponse extends QuizResponse {

    private List<ChoiceResponse> choices = new ArrayList<>();

    @Builder
    public MultipleQuizResponse(Long id, int number, String question, String image) {
        super(id, number, question, image);
    }

    public void addChoices(List<ChoiceResponse> choiceResponses) {
        this.choices.addAll(choiceResponses);
    }

    public static MultipleQuizResponse from(Quiz quiz) {
        return new MultipleQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                quiz.getPhotoUrl()
        );
    }
}
