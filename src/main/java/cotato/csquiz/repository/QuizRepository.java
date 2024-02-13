package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Quiz;
import java.util.List;

import cotato.csquiz.domain.enums.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByEducationId(Long educationId);

    void deleteAllByEducationId(Long educationId);

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findByStart(QuizStatus quizStatus);

    List<Quiz> findByGeneration(Generation generation);
}
