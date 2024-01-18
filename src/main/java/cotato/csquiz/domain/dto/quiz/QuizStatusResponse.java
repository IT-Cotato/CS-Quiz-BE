package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.QuizStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class QuizStatusResponse {
    private QuizStatus status;
    private QuizStatus start;
    private long quizNum;
    private String command;
}
