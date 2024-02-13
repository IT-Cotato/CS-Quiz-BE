package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {
    List<Scorer> findAllByQuiz(Quiz quiz);

    List<Scorer> findAllByQuizAndMember(Quiz quiz, Member member);

    Scorer findByQuiz(Quiz quiz);
}
