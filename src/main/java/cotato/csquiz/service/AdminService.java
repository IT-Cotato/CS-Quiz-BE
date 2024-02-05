package cotato.csquiz.service;

import static cotato.csquiz.domain.entity.MemberRole.REFUSED;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberApproveRequest;
import cotato.csquiz.domain.dto.member.MemberEnrollInfoResponse;
import cotato.csquiz.domain.dto.member.MemberRejectRequest;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.domain.dto.member.UpdateOldMemberRoleRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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
                        .role(member.getRole())
                        .build())
                .toList();
    }

    @Transactional
    public void approveApplicant(MemberApproveRequest memberApproveRequest) {
        Member member = findMember(memberApproveRequest.getUserId());
        Generation findGeneration = generationRepository.findById(memberApproveRequest.getGenerationId())
                .orElseThrow(() -> new AppException(ErrorCode.GENERATION_NOT_FOUND));
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(findGeneration);
            member.updatePosition(memberApproveRequest.getPosition());
            memberRepository.save(member);
        }
    }

    @Transactional
    public void rejectApplicant(MemberRejectRequest memberRejectRequest) {
        Member member = findMember(memberRejectRequest.getUserId());
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(REFUSED);
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

    public List<MemberEnrollInfoResponse> getCurrentActiveMembers() {
        List<Member> activeMembers = memberRepository.findAllByRole(MemberRole.MEMBER);
        return activeMembers.stream()
                .map(MemberEnrollInfoResponse::from)
                .toList();
    }

    @Transactional
    public void updateActiveMemberRole(UpdateActiveMemberRoleRequest updateActiveMemberRoleRequest) {
        Member member = findMember(updateActiveMemberRoleRequest.getUserId());
        if (member.getRole() == MemberRole.GENERAL || member.getRole() == MemberRole.REFUSED) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        member.updateRole(updateActiveMemberRoleRequest.getRole());
        memberRepository.save(member);
    }

    public List<MemberEnrollInfoResponse> getOldMembersList() {
        List<Member> oldMembers = memberRepository.findAllByRole(MemberRole.OLD_MEMBER);
        return oldMembers.stream()
                .map(MemberEnrollInfoResponse::from)
                .toList();
    }

    @Transactional
    public void updateOldMemberToActiveGeneration(UpdateOldMemberRoleRequest updateOldMemberRoleRequest) {
        Member member = findMember(updateOldMemberRoleRequest.getUserId());
        validateIsOldMember(member);
        if (member.getRole() == MemberRole.OLD_MEMBER) {
            member.updateRole(MemberRole.MEMBER);
            memberRepository.save(member);
        }
    }

    private void validateIsOldMember(Member member) {
        if (member.getRole() != MemberRole.OLD_MEMBER) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_OLD_MEMBER);
        }
    }
}

