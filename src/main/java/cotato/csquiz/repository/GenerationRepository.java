package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
}
