package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.LoginInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

    Optional<LoginInfo> findByEmail(String email);

    Optional<LoginInfo> findByPhoneNum(String phone);
}
