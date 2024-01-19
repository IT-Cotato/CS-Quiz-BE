package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByEducationId(Long educationId);

    void deleteAllByEducationId(Long educationId);
}
