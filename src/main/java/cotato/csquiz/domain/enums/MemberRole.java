package cotato.csquiz.domain.enums;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum MemberRole {

    GENERAL("ROLE_GENERAL"),
    REFUSED("ROLE_REFUSED"),
    OLD_MEMBER("ROLE_OM"),

    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN"),
    EDUCATION("ROLE_EDUCATION");

    private final String key;

    MemberRole(String key) {
        this.key = key;
    }

    public static MemberRole fromKey(final String key) {
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> memberRole.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ENUM_NOT_RESOLVED));
    }
}
