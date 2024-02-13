package cotato.csquiz.domain.dto.quiz;

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

    public void addChoice(ChoiceResponse choiceResponse) {
        this.choices.add(choiceResponse);
    }
}
