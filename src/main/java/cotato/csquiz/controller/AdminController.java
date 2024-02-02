package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.member.ActiveMemberInfoResponse;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberApproveDto;
import cotato.csquiz.domain.dto.member.UpdateActiveMemberRoleDto;
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
    public ResponseEntity<List<MemberInfoResponse>> getApplicantList() {
        log.info("가입자 확인 컨트롤러");
        List<MemberInfoResponse> applicantList = adminService.getApplicantList();
        return ResponseEntity.ok().body(applicantList);
    }

    @PatchMapping("/approve")
    public ResponseEntity<?> approveApplicant(@RequestBody MemberApproveDto memberApproveDto) {
        log.info("가입자 승인 컨트롤러, 요청된 member id : {}", memberApproveDto.getUserId());
        adminService.approveApplicant(memberApproveDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<?> rejectApplicant(@RequestBody MemberApproveDto memberApproveDto) {
        log.info("가입자 거절 컨트롤러, 요청된 member id : {}", memberApproveDto.getUserId());
        adminService.rejectApplicant(memberApproveDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active-members")
    public ResponseEntity<List<ActiveMemberInfoResponse>> getCurrentActiveMembers() {
        log.info("현재 활동 중인 부원 목록 조회 컨트롤러");
        List<ActiveMemberInfoResponse> activeMembers = adminService.getCurrentActiveMembers();
        return ResponseEntity.ok().body(activeMembers);
    }

    @PatchMapping("/active-members/update-role")
    public ResponseEntity<?> updateActiveMemberRole(@RequestBody UpdateActiveMemberRoleDto updateActiveMemberRoleDto) {
        log.info("현재 활동 중인 부원 역할 업데이트 컨트롤러, 대상 member id : {}", updateActiveMemberRoleDto.getUserId());
        adminService.updateActiveMemberRole(updateActiveMemberRoleDto);
        return ResponseEntity.ok().build();
    }
}
