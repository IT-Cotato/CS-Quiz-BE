package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.ShortAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortAnswerRepository extends JpaRepository<ShortAnswer, Long> {
}
