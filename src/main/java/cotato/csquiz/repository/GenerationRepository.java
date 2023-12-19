package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    @Query("SELECT g.name FROM Generation g")
    List<String> findAllGenerationNames();
}
