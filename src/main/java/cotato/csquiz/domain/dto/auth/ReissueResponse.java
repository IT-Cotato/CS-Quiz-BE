package cotato.csquiz.domain.dto.auth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ReissueResponse(
        String accessToken
) {
    public static ReissueResponse from(String accessToken) {
        log.info("[액세스 토큰 발급 완료], {}", accessToken);
        return new ReissueResponse(accessToken);
    }
}
