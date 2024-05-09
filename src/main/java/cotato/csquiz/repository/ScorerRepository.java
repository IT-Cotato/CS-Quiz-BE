package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {
    Optional<Scorer> findByQuiz(Quiz quiz);

    List<Scorer> findAllByQuiz(Quiz quiz);

    @Transactional
    @Modifying
    @Query("select s from Scorer s where s.quiz.id in :quizIds")
    List<Scorer> findAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);
}
