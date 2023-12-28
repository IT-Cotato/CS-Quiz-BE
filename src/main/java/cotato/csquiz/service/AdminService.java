package cotato.csquiz.service;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public List<MemberInfoResponse> getApplicantList() {
        List<Member> applicantList = memberRepository.findAll(); // 필요에 따라 조건을 추가하여 신청자 리스트를 가져오세요.

        return applicantList.stream()
                .map(member -> MemberInfoResponse.builder()
                        .name(member.getName())
                        .backFourNumber(member.getPhoneNumber().substring(member.getPhoneNumber().length() - 4))
                        .build())
                .collect(Collectors.toList());
    }

    private void logMemberInfo(MemberInfoResponse memberInfoResponse) {
        log.info("이름 + 번호 4자리: {}({})", memberInfoResponse.getName(), memberInfoResponse.getBackFourNumber());
    }

    public MemberInfoResponse getMemberInfo(String email) {
        // MemberService의 getMemberInfo를 사용하여 개별 멤버의 정보를 가져옴
        return memberService.getMemberInfo(email);
    }
}

