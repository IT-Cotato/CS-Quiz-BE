package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.ShortAnswer;
import cotato.csquiz.domain.entity.ShortQuiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortAnswerRepository extends JpaRepository<ShortAnswer, Long> {
    List<ShortAnswer> findAllByShortQuiz(ShortQuiz shortQuiz);
}
