package cotato.csquiz.domain.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    GENERAL("GENERAL"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN"),
    EDUCATION("EDUCATION");

    private final String key;

    MemberRole(String key) {
        this.key = key;
    }
}
