package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberMyPageInfoResponse;
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

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberInfoResponse getMemberInfo(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("이름 + 번호 4자리: {}({})", findMember.getName(), findMember.getBackFourNumber());
        return MemberInfoResponse.from(findMember);
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
        validatePassword(findMember.getPassword(), password);
        findMember.updatePassword(bCryptPasswordEncoder.encode(password));
    }

    private void validatePassword(String originPassword, String newPassword) {
        if (!isProperLength(newPassword)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        if (bCryptPasswordEncoder.matches(newPassword, originPassword)) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
    }

    private boolean isProperLength(String newPassword) {
        return MIN_LENGTH <= newPassword.length() && newPassword.length() <= MAX_LENGTH;
    }

    public MemberMyPageInfoResponse findMyPageInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberMyPageInfoResponse.from(findMember);
    }
}
