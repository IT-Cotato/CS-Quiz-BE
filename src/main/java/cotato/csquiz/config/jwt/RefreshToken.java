package cotato.csquiz.config.jwt;

import jakarta.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 3)
public class RefreshToken {

    @Id
    private String id;

    private Set<String> refreshToken;

    public RefreshToken(String id) {
        this.id = id;
        this.refreshToken = new HashSet<>();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken.add(refreshToken);
    }
}
