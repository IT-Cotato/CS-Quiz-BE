package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.QuizStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByEducationId(Long educationId);

    void deleteAllByEducationId(Long educationId);

    List<Quiz> findByStatus(QuizStatus status);

    Optional<Quiz> findByStatusAndEducation(QuizStatus status, Education education);

    List<Quiz> findByStart(QuizStatus quizStatus);

    @Query("select o from Quiz o join fetch o.records")
    List<Quiz> findAllFetchJoinRecords();

    @Query("select o from Quiz o join fetch o.scorer")
    List<Quiz> findAllFetchJoinByScorer();
}
