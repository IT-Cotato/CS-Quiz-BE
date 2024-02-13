package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.entity.MultipleQuiz;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findAllByMultipleQuiz(MultipleQuiz multipleQuiz);

    Optional<Choice> findByMultipleQuizAndChoiceNumber(MultipleQuiz multipleQuiz, int choiceNumber);
}
