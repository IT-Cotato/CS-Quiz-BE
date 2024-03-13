package cotato.csquiz.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChoiceCorrect {
    ANSWER("정답"),
    NO_ANSWER("정답이 아님"),
    SECRET("비밀")
    ;

    private final String description;
}
