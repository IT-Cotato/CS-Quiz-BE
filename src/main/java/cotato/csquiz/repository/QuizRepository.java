package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.QuizStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByEducationId(Long educationId);

    void deleteAllByEducationId(Long educationId);

    @Modifying
    @Query("delete from Quiz q where q.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findByStart(QuizStatus quizStatus);

    @Query("select o from Quiz o join fetch o.records")
    List<Quiz> findAllFetchJoinRecords();

    @Query("select o from Quiz o join fetch o.scorer")
    List<Quiz> findAllFetchJoinByScorer();
}
