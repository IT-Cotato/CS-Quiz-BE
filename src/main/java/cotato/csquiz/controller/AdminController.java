package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.ApplyMemberInfo;
import cotato.csquiz.domain.dto.member.MemberApproveRequest;
import cotato.csquiz.domain.dto.member.MemberEnrollInfoResponse;
import cotato.csquiz.domain.dto.member.MemberRejectRequest;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberToOldMemberRequest;
import cotato.csquiz.domain.dto.member.UpdateOldMemberRoleRequest;
import cotato.csquiz.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/applicants")
    public ResponseEntity<?> getApplicantList() {
        List<ApplyMemberInfo> applicantList = adminService.getApplicantList();
        return ResponseEntity.ok().body(applicantList);
    }

    @GetMapping("/reject-applicants")
    public ResponseEntity<?> rejectApplicantList() {
        List<ApplyMemberInfo> applicantList = adminService.getRejectApplicantList();
        return ResponseEntity.ok().body(applicantList);
    }

    @PatchMapping("/approve")
    public ResponseEntity<?> approveApplicant(@RequestBody MemberApproveRequest memberApproveRequest) {
        log.info("[가입자 승인 컨트롤러, 요청된 member id : {}]", memberApproveRequest.getMemberId());
        adminService.approveApplicant(memberApproveRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<?> rejectApplicant(@RequestBody MemberRejectRequest memberRejectRequest) {
        log.info("[가입자 거절 컨트롤러, 요청된 member id : {}]", memberRejectRequest.getMemberId());
        adminService.rejectApplicant(memberRejectRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reapprove")
    public ResponseEntity<?> reapproveApplicant(@RequestBody MemberApproveRequest memberApproveRequest) {
        log.info("[가입자 재승인 컨트롤러, 요청된 member id : {}]", memberApproveRequest.getMemberId());
        adminService.reapproveApplicant(memberApproveRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> getCurrentActiveMembers() {
        List<MemberEnrollInfoResponse> activeMembers = adminService.findCurrentActiveMembers();
        return ResponseEntity.ok().body(activeMembers);
    }

    @PatchMapping("/active-members/role")
    public ResponseEntity<?> updateActiveMemberRole(
            @RequestBody UpdateActiveMemberRoleRequest updateActiveMemberRoleRequest) {
        log.info("[현재 활동 중인 부원 역할 업데이트 컨트롤러, 대상 member id : {}]", updateActiveMemberRoleRequest.getMemberId());
        adminService.updateActiveMemberRole(updateActiveMemberRoleRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/active-members/to-old-members")
    public ResponseEntity<?> updateActiveMemberToOldMember(
            @RequestBody UpdateActiveMemberToOldMemberRequest updateActiveMemberToOldMemberRequest) {
        log.info("[현재 활동 중인 부원들을 OM으로 업데이트 하는 컨트롤러, 대상 member ids : {}]",
                updateActiveMemberToOldMemberRequest.getMemberIds());
        adminService.updateActiveMemberToOldMember(updateActiveMemberToOldMemberRequest.getMemberIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/old-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> getOldMembersList() {
        List<MemberEnrollInfoResponse> oldMembersList = adminService.getOldMembersList();
        return ResponseEntity.ok().body(oldMembersList);
    }

    @PatchMapping("/old-members/role")
    public ResponseEntity<?> updateOldMemberToActiveGeneration(
            @RequestBody UpdateOldMemberRoleRequest updateOldMemberRoleRequest) {
        log.info("[OM을 현재 활동 기수로 업데이트하는 컨트롤러, 대상 member id: {}]", updateOldMemberRoleRequest.getMemberId());
        adminService.updateOldMemberToActiveGeneration(updateOldMemberRoleRequest);
        return ResponseEntity.ok().build();
    }
}
