package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberInfoResponse getMemberInfo(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        int numberLength = findMember.getPhoneNumber().length();
        String lastFourNumber = findMember.getPhoneNumber().substring(numberLength - 4);
        log.info("이름 + 번호 4자리: {}({})", findMember.getName(), lastFourNumber);

        return MemberInfoResponse.builder()
                .id(findMember.getId())
                .name(findMember.getName())
                .backFourNumber(lastFourNumber)
                .role(findMember.getRole())
                .build();
    }

    public void checkCorrectPassword(String accessToken, String password) {
        if (jwtUtil.isExpired(accessToken)) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String email = jwtUtil.getEmail(accessToken);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        if (!bCryptPasswordEncoder.matches(password, findMember.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Transactional
    public void updatePassword(String accessToken, String password) {
        if (jwtUtil.isExpired(accessToken)) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String email = jwtUtil.getEmail(accessToken);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        validateSamePassword(findMember.getPassword(), password);
        findMember.updatePassword(bCryptPasswordEncoder.encode(password));
    }

    private void validateSamePassword(String originPassword, String newPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, originPassword)) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
    }
}
