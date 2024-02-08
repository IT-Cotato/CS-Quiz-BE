package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.RefusedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefusedMemberRepository extends JpaRepository<RefusedMember, Long> {
}
