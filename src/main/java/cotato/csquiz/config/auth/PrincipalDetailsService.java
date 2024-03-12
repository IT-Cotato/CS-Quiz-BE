package cotato.csquiz.config.auth;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        log.info("해당 유저의 로그인 요청: {}", findMember.getEmail());
        return new PrincipalDetails(findMember);
    }
}
