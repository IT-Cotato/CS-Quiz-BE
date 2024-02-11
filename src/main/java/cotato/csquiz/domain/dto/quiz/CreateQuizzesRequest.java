package cotato.csquiz.domain.dto.quiz;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizzesRequest {

    private List<MultipleChoiceQuizRequest> multiples = new ArrayList<>();
    private List<CreateShortQuizRequest> shortQuizzes = new ArrayList<>();
}
