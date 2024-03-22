package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    Optional<Education> findBySession(Session session);
}
