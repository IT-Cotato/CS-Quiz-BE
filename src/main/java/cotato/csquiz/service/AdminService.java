package cotato.csquiz.service;

import static cotato.csquiz.domain.enums.MemberRole.OLD_MEMBER;
import static cotato.csquiz.domain.enums.MemberRole.REFUSED;

import cotato.csquiz.domain.dto.auth.ApplyMemberInfo;
import cotato.csquiz.domain.dto.member.MemberApproveRequest;
import cotato.csquiz.domain.dto.member.MemberEnrollInfoResponse;
import cotato.csquiz.domain.dto.member.MemberRejectRequest;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.domain.dto.member.UpdateOldMemberRoleRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.RefusedMember;
import cotato.csquiz.domain.enums.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.RefusedMemberRepository;
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
    private final RefusedMemberRepository refusedMemberRepository;

    public List<ApplyMemberInfo> getApplicantList() {
        List<Member> applicantList = memberRepository.findAllByRole(MemberRole.GENERAL);
        return buildApplyInfoList(applicantList);
    }

    public List<ApplyMemberInfo> getRejectApplicantList() {
        List<Member> applicantList = memberRepository.findAllByRole(REFUSED);
        return buildApplyInfoList(applicantList);
    }

    @Transactional
    public void approveApplicant(MemberApproveRequest memberApproveRequest) {
        Member member = findMember(memberApproveRequest.getUserId());
        Generation findGeneration = getGeneration(memberApproveRequest.getGenerationId());
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(findGeneration);
            member.updatePosition(memberApproveRequest.getPosition());
            memberRepository.save(member);
        }
    }

    @Transactional
    public void reapproveApplicant(MemberApproveRequest memberApproveRequest) {
        Member member = findMember(memberApproveRequest.getUserId());
        if (member.getRole() == REFUSED) {
            Generation findGeneration = getGeneration(memberApproveRequest.getGenerationId());
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(findGeneration);
            member.updatePosition(memberApproveRequest.getPosition());
            deleteRefusedMember(member);
        }
    }

    @Transactional
    public void rejectApplicant(MemberRejectRequest memberRejectRequest) {
        Member member = findMember(memberRejectRequest.getUserId());
        validateIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(REFUSED);
            memberRepository.save(member);
            addRefusedMember(member);
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
        Member member = findMember(updateActiveMemberRoleRequest.getMemberId());
        if (member.getRole() == MemberRole.GENERAL || member.getRole() == REFUSED || member.getRole() == OLD_MEMBER) {
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

    private void addRefusedMember(Member member) {
        RefusedMember refusedMember = RefusedMember.builder()
                .member(member)
                .build();
        refusedMemberRepository.save(refusedMember);
    }

    private void deleteRefusedMember(Member member) {
        RefusedMember refusedMember = refusedMemberRepository.findByMember(member)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        refusedMemberRepository.delete(refusedMember);
    }

    private Generation getGeneration(Long generationId) {
        return generationRepository.findById(generationId)
                .orElseThrow(() -> new AppException(ErrorCode.GENERATION_NOT_FOUND));
    }

    private static List<ApplyMemberInfo> buildApplyInfoList(List<Member> applicantList) {
        return applicantList.stream()
                .map(ApplyMemberInfo::from)
                .toList();
    }
}

