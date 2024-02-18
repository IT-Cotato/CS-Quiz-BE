package cotato.csquiz.domain.dto.socket;

import lombok.Getter;

public record SocketTokenDto(
        String socketToken
) {
    public static SocketTokenDto of(String socketToken) {
        return new SocketTokenDto(
                socketToken
        );
    }
}
