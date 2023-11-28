package cotato.csquiz.service;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateService {

    private final MemberRepository memberRepository;

    public void checkDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            log.info("[회원 가입 실패]: 중복된 이메일 " + email);
            throw new AppException(ErrorCode.EMAIL_DUPLICATED);
        }
    }

    public void checkDuplicatePhoneNumber(String phone) {
        if (memberRepository.findByPhoneNumber(phone).isPresent()) {
            log.info("[회원 가입 실패]: 존재하는 전화번호 " + phone);
            throw new AppException(ErrorCode.PHONE_NUMBER_DUPLICATED);
        }
    }
}
