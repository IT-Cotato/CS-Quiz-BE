package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Scorer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {
}
