package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/applicants")
    public ResponseEntity<List<MemberInfoResponse>> getApplicantList() {
        List<MemberInfoResponse> applicantList = adminService.getApplicantList();
        return ResponseEntity.ok().body(applicantList);
    }

    @PatchMapping("/approve")
    public ResponseEntity<?> approveApplicant(@RequestParam("userId") Long userId) {
        log.info("가입자 승인 컨트롤러, 요청된 member id : {}", userId);
        adminService.approveApplicant(userId);

    }

    @PatchMapping("/reject")
    public ResponseEntity<?> rejectApplicant(@RequestParam("userId") Long userId) {
        log.info("가입자 거절 컨트롤러, 요청된 member id : {}", userId);
        adminService.rejectApplicant(userId);
    }
}
