package cotato.csquiz.domain.dto.quiz;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortQuizResponse extends QuizResponse {

    private List<ShortAnswerResponse> shortAnswers = new ArrayList<>();

    @Builder
    public ShortQuizResponse(Long id, int number, String question, String image) {
        super(id, number, question, image);
    }

    public void addShortAnswers(ShortAnswerResponse shortAnswerResponse) {
        this.shortAnswers.add(shortAnswerResponse);
    }
}
