package cotato.csquiz.service;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void approveApplicant(Long userId) {
        Member member = findMember(userId);
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.MEMBER);
            memberRepository.save(member);
        }
    }

    @Transactional
    public void rejectApplicant(Long userId) {
        Member member = findMember(userId);
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.REFUSED);
            memberRepository.save(member);
        }
    }

    private Member findMember(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateIsGeneral(Member member) {
        if (member.getRole() != MemberRole.GENERAL) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
    }
}

