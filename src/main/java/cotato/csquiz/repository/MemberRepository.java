package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhoneNumber(String phone);
    List<Member> findAllByRole(MemberRole memberRole);
}
