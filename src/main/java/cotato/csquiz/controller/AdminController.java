package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.domain.dto.member.MemberApproveRequest;
import cotato.csquiz.domain.dto.member.MemberRejectRequest;
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
    public ResponseEntity<?> approveApplicant(@RequestBody MemberApproveRequest memberApproveRequest) {
        log.info("가입자 승인 컨트롤러, 요청된 member id : {}", memberApproveRequest.getUserId());
        adminService.approveApplicant(memberApproveRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<?> rejectApplicant(@RequestBody MemberRejectRequest memberRejectRequest) {
        log.info("가입자 거절 컨트롤러, 요청된 member id : {}", memberRejectRequest.getUserId());
        adminService.rejectApplicant(memberRejectRequest);
        return ResponseEntity.ok().build();
    }
}
