package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
