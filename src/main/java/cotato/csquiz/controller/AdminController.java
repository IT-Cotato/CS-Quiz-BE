package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.auth.ApplyMemberInfo;
import cotato.csquiz.controller.dto.member.MemberApproveRequest;
import cotato.csquiz.controller.dto.member.MemberEnrollInfoResponse;
import cotato.csquiz.controller.dto.member.MemberRejectRequest;
import cotato.csquiz.controller.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.controller.dto.member.UpdateActiveMemberToOldMemberRequest;
import cotato.csquiz.controller.dto.member.UpdateOldMemberRoleRequest;
import cotato.csquiz.service.AdminService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ApplyMemberInfo>> getApplicantList() {
        return ResponseEntity.ok().body(adminService.getApplicantList());
    }

    @GetMapping("/reject-applicants")
    public ResponseEntity<List<ApplyMemberInfo>> rejectApplicantList() {
        return ResponseEntity.ok().body(adminService.getRejectApplicantList());
    }

    @PatchMapping("/approve")
    public ResponseEntity<Void> approveApplicant(@RequestBody @Valid MemberApproveRequest memberApproveRequest) {
        log.info("[가입자 승인 컨트롤러, 요청된 member id : {}]", memberApproveRequest.memberId());
        adminService.approveApplicant(memberApproveRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectApplicant(@RequestBody @Valid MemberRejectRequest memberRejectRequest) {
        log.info("[가입자 거절 컨트롤러, 요청된 member id : {}]", memberRejectRequest.memberId());
        adminService.rejectApplicant(memberRejectRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reapprove")
    public ResponseEntity<Void> reApproveApplicant(@RequestBody @Valid MemberApproveRequest memberApproveRequest) {
        log.info("[가입자 재승인 컨트롤러, 요청된 member id : {}]", memberApproveRequest.memberId());
        adminService.reApproveApplicant(memberApproveRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> getCurrentActiveMembers() {
        return ResponseEntity.ok().body(adminService.findCurrentActiveMembers());
    }

    @PatchMapping("/active-members/role")
    public ResponseEntity<Void> updateActiveMemberRole(
            @RequestBody @Valid UpdateActiveMemberRoleRequest updateActiveMemberRoleRequest) {
        log.info("[현재 활동 중인 부원 역할 업데이트 컨트롤러, 대상 member id : {}]", updateActiveMemberRoleRequest.memberId());
        adminService.updateActiveMemberRole(updateActiveMemberRoleRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/active-members/to-old-members")
    public ResponseEntity<Void> updateActiveMemberToOldMember(
            @RequestBody @Valid UpdateActiveMemberToOldMemberRequest updateActiveMemberToOldMemberRequest) {
        log.info("[현재 활동 중인 부원들을 OM으로 업데이트 하는 컨트롤러, 대상 member ids : {}]",
                updateActiveMemberToOldMemberRequest.memberIds());
        adminService.updateActiveMemberToOldMember(updateActiveMemberToOldMemberRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/old-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> getOldMembersList() {
        return ResponseEntity.ok().body(adminService.getOldMembersList());
    }

    @PatchMapping("/old-members/role")
    public ResponseEntity<Void> updateOldMemberToActiveGeneration(
            @RequestBody @Valid UpdateOldMemberRoleRequest updateOldMemberRoleRequest) {
        log.info("[OM을 현재 활동 기수로 업데이트하는 컨트롤러, 대상 member id: {}]", updateOldMemberRoleRequest.memberId());
        adminService.updateOldMemberToActiveGeneration(updateOldMemberRoleRequest);
        return ResponseEntity.noContent().build();
    }
}
