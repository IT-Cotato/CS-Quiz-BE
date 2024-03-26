package cotato.csquiz.domain.dto.record;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReplyRequest(
        @NotNull
        Long quizId,
        @NotNull
        Long memberId,
        @NotNull
        List<String> inputs
) {
}
