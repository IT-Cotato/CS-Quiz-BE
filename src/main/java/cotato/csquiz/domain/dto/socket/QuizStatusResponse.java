package cotato.csquiz.domain.dto.socket;

import cotato.csquiz.domain.enums.QuizStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStatusResponse {

    private QuizStatus status;
    private QuizStatus start;
    private Long quizId;
    private String command;
}
