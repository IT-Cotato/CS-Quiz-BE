package cotato.csquiz.domain.dto.socket;

import cotato.csquiz.domain.entity.QuizStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class QuizStatusResponse {
    private QuizStatus status;
    private QuizStatus start;
    private long quizNum;
    private String command;
}
