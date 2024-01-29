package cotato.csquiz.domain.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    REFUSED("ROLE_REFUSED"),
    GENERAL("ROLE_GENERAL"),
    MEMBER("ROLE_MEMBER"),
    OLD_MEMBER("ROLE_OM"),
    ADMIN("ROLE_ADMIN"),
    EDUCATION("ROLE_EDUCATION");

    private final String key;

    MemberRole(String key) {
        this.key = key;
    }
}
