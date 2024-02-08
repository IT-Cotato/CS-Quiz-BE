package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.RefusedMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefusedMemberRepository extends JpaRepository<RefusedMember, Long> {
    Optional<RefusedMember> findByMember(Member member);
}
