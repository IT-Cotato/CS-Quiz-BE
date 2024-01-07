package cotato.csquiz.service;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;

    public List<MemberInfoResponse> getApplicantList() {
        List<Member> applicantList = memberRepository.findAll();

        return applicantList.stream()
                .filter(member -> member.getRole() == MemberRole.GENERAL)
                .map(member -> MemberInfoResponse.builder()
                        .id(member.getId())
                        .name(member.getName())
                        .backFourNumber(member.getPhoneNumber().substring(member.getPhoneNumber().length() - 4))
                        .build())
                .collect(Collectors.toList());
    }


    private Member findMember(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 ID에 해당하는 회원을 찾을 수 없습니다."));
    }

    public void approveApplicant(Long userId) {
        Member member = findMember(userId);

        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.MEMBER);
            memberRepository.save(member);
        }
    }

    public void rejectApplicant(Long userId) {
        Member member = findMember(userId);

        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.REFUSED);
            memberRepository.save(member);
        }
    }

}

