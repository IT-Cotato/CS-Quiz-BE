package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.enums.QuizType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleQuizResponse extends QuizResponse {

    private List<ChoiceResponse> choices = new ArrayList<>();

    @Builder
    public MultipleQuizResponse(Long id, int number, QuizType type, String question, String photoUrl) {
        super(id, number, type, question, photoUrl);
    }

    public void addChoice(ChoiceResponse choiceResponse) {
        this.choices.add(choiceResponse);
    }
}
