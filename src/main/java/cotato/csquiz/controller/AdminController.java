package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/api/admin", method = RequestMethod.GET)
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/applicant-list")
    public ResponseEntity<List<MemberInfoResponse>> getApplicantList() {
        List<MemberInfoResponse> applicantList = adminService.getApplicantList();
        return ResponseEntity.ok().body(applicantList);
    }

    @GetMapping("/member-info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@RequestParam String email) {
        MemberInfoResponse memberInfo = adminService.getMemberInfo(email);
        return ResponseEntity.ok().body(memberInfo);
    }
}
