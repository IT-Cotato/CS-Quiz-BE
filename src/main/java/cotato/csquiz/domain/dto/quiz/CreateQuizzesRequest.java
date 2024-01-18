package cotato.csquiz.domain.dto.quiz;

import java.util.List;
import lombok.Data;

@Data
public class CreateQuizzesRequest {

    private List<MultipleChoiceQuizRequest> multiples;
    private List<CreateShortQuizRequest> shortQuizzes;
}
