package cotato.csquiz.service;

import cotato.csquiz.domain.dto.member.ActiveMemberInfoResponse;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberApproveDto;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MemberPosition;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final GenerationRepository generationRepository;

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
    public void approveApplicant(MemberApproveDto memberApproveDto) {
        Member member = findMember(memberApproveDto.getUserId());
        Optional<Generation> generation = generationRepository.findByName(memberApproveDto.getGenerationName());
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(generation.get());
            member.updatePosition(MemberPosition.valueOf(memberApproveDto.getPosition()));
            memberRepository.save(member);
        }
    }

    @Transactional
    public void rejectApplicant(MemberApproveDto memberApproveDto) {
        Member member = findMember(memberApproveDto.getUserId());
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

    public List<ActiveMemberInfoResponse> getCurrentActiveMembers() {
        Optional<Member> activeMembers = memberRepository.findAllByRole(MemberRole.MEMBER);

        return activeMembers.stream()
                .map(member -> {
                    Generation generation = member.getGeneration();
                    return ActiveMemberInfoResponse.builder()
                            .id(member.getId())
                            .name(member.getName())
                            .position(member.getPosition())
                            .generationName(generation != null ? generation.getName() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateActiveMemberRole(UpdateActiveMemberRoleRequest updateActiveMemberRoleRequest) {
        Member member = findMember(updateActiveMemberRoleRequest.getUserId());
        member.updateRole(MemberRole.valueOf(updateActiveMemberRoleRequest.getRole()));
        memberRepository.save(member);
    }
}

