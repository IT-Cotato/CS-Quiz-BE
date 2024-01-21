package cotato.csquiz.domain.dto.quiz;

import lombok.Builder;

@Builder
public record ShortAnswerResponse(
        String answer
) {
}
