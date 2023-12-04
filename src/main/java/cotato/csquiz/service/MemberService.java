package cotato.csquiz.service;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResponse getMemberInfo(String email){
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        int numberLength = findMember.getPhoneNumber().length();
        String lastFourNumber = findMember.getPhoneNumber().substring(numberLength - 4);
        log.info("이름 + 번호 4자리: {}({})",findMember.getName(),lastFourNumber);
        return MemberInfoResponse.builder()
                .id(findMember.getId())
                .name(findMember.getName())
                .backFourNumber(lastFourNumber)
                .build();
    }
}
