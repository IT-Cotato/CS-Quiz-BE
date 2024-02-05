package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
