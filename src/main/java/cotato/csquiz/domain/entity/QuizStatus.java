package cotato.csquiz.domain.entity;

import lombok.Getter;

@Getter
public enum QuizStatus {
    QUIZ_ON("ON"),
    QUIZ_OFF("OFF");

    private final String status;

    QuizStatus(String status) {
        this.status = status;
    }
}
