package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByGeneration(Generation generation);

    List<Session> findAllByGenerationId(Long generationId);
}
