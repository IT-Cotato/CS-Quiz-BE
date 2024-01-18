package cotato.csquiz.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuizStatus {
    ON("ON"),
    OFF("OFF");

    private final String status;
}
