package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberMyPageInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final EncryptService encryptService;
    private final ValidateService validateService;

    public MemberInfoResponse getMemberInfo(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원을 찾을 수 없습니다."));

        String rawBackFourNumber = getBackFourNumber(findMember);
        log.info("이름 + 번호 4자리: {}({})", findMember.getName(), rawBackFourNumber);
        return MemberInfoResponse.from(findMember, rawBackFourNumber);
    }

    public String getBackFourNumber(Member member) {
        String decryptedPhone = member.getPhoneNumber();
        String originPhoneNumber = encryptService.decryptPhoneNumber(decryptedPhone);
        int numberLength = originPhoneNumber.length();
        return originPhoneNumber.substring(numberLength - 4);
    }

    public void checkCorrectPassword(String accessToken, String password) {
        String email = jwtUtil.getEmail(accessToken);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원을 찾을 수 없습니다."));
        if (!bCryptPasswordEncoder.matches(password, findMember.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Transactional
    public void updatePassword(String accessToken, String password) {
        String email = jwtUtil.getEmail(accessToken);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원을 찾을 수 없습니다."));
        validateService.checkPasswordPattern(password);
        validateIsSameBefore(findMember.getPassword(), password);
        
        findMember.updatePassword(bCryptPasswordEncoder.encode(password));
    }

    private void validateIsSameBefore(String originPassword, String newPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, originPassword)) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
    }

    public MemberMyPageInfoResponse findMyPageInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        String originPhoneNumber = encryptService.decryptPhoneNumber(findMember.getPhoneNumber());
        return MemberMyPageInfoResponse.from(findMember, originPhoneNumber);
    }
}
