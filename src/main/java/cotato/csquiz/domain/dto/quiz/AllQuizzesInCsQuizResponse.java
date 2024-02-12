package cotato.csquiz.domain.dto.quiz;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AllQuizzesInCsQuizResponse {
    List<CsAdminQuizResponse> quizzes;
}
