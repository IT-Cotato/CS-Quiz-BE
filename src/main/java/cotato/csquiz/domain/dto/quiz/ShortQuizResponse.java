package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.enums.QuizType;
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
    public ShortQuizResponse(Long id, int number, QuizType type, String question, String photoUrl) {
        super(id, number, type, question, photoUrl);
    }

    public void addShortAnswers(ShortAnswerResponse shortAnswerResponse) {
        this.shortAnswers.add(shortAnswerResponse);
    }
}
