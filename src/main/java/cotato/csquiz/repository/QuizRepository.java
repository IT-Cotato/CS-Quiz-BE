package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.QuizStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Transactional
    @Modifying
    @Query("delete from Quiz p where p.id in :ids")
    void deleteAllByQuizIdsInQuery(@Param("ids") List<Long> ids);

    List<Quiz> findAllByEducationId(Long educationId);

    List<Quiz> findByStatus(QuizStatus status);

    Optional<Quiz> findByStatusAndEducation(QuizStatus status, Education education);

    List<Quiz> findByStart(QuizStatus quizStatus);

    @Query("select o from Quiz o join fetch o.records")
    List<Quiz> findAllFetchJoinRecords();

    @Query("select o from Quiz o join fetch o.scorer")
    List<Quiz> findAllFetchJoinByScorer();
}
