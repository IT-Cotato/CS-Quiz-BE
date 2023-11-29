package cotato.csquiz.domain.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    GENERAL("ROLE_GENERAL"),
    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN"),
    EDUCATION("ROLE_EDUCATION");

    private final String key;

    MemberRole(String key) {
        this.key = key;
    }
}
