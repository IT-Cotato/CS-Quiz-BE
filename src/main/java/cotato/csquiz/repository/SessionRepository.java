package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session,Long> {
    List<Session> findAllByGeneration(Generation generation);
}
