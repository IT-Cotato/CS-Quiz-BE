package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.entity.QuizType;
import lombok.Builder;

@Builder
public record QuizResponse(
        Long id,
        QuizType type,
        int number,
        String question,
        String photoUrl
) {
}
