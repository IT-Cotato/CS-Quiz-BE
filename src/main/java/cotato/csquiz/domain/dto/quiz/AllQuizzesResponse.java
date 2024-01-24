package cotato.csquiz.domain.dto.quiz;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AllQuizzesResponse {

    private List<MultipleQuizResponse> multiples = new ArrayList<>();
    private List<ShortQuizResponse> shortQuizzes = new ArrayList<>();
}
