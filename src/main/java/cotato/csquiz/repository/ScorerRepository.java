package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {
    Optional<Scorer> findByQuiz(Quiz quiz);

    List<Scorer> findAllByQuiz(Quiz quiz);
}
