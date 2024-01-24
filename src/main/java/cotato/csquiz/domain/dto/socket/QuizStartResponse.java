package cotato.csquiz.domain.dto.socket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStartResponse {
    private long quizNumber;
    private String command;
}
