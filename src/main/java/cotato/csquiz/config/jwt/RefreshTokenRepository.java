package cotato.csquiz.config.jwt;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String > {

    Optional<RefreshToken> findByAccessToken(String accessToken);
}
