package cotato.csquiz.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberPosition {
    BE("Back-end"),
    FE("Front-end"),
    Design("Design"),
    PM("Product-Manager");

    private final String fullName;
}
