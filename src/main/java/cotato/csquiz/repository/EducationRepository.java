package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Session;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    Optional<Education> findEducationBySession(Session session);
    List<Education> findBySession_Generation_Id(Long generationId);
}
