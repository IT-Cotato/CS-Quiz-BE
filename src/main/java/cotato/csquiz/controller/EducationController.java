package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.AllEducationResponse;
import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.AddEducationResponse;
import cotato.csquiz.domain.dto.education.GetStatusResponse;
import cotato.csquiz.domain.dto.education.PatchEducationRequest;
import cotato.csquiz.domain.dto.education.UpdateEducationRequest;
import cotato.csquiz.domain.dto.education.WinnerInfoResponse;
import cotato.csquiz.domain.dto.quiz.KingMemberInfo;
import cotato.csquiz.service.EducationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestParam(value = "educationId") Long educationId) {
        GetStatusResponse response = educationService.getStatus(educationId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<AddEducationResponse> addEducation(@RequestBody AddEducationRequest request) {
        AddEducationResponse response = educationService.addEducation(request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping
    public ResponseEntity<?> updateEducation(@RequestBody UpdateEducationRequest request) {
        educationService.updateSubjectAndNumber(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getEducationListByGeneration(@RequestParam(value = "generationId") Long generationId) {
        List<AllEducationResponse> educationList = educationService.getEducationListByGeneration(generationId);
        return ResponseEntity.ok().body(educationList);
    }

    @PatchMapping("/status")
    public ResponseEntity<?> patchEducationStatus(@RequestBody PatchEducationRequest request) {
        educationService.patchEducationStatus(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/result/kings")
    public ResponseEntity<?> findFinalKingMembers(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 조회 컨트롤러]", educationId);
        List<KingMemberInfo> kingMemberInfos = educationService.findKingMemberInfo(educationId);
        return ResponseEntity.ok().body(kingMemberInfos);
    }

    @GetMapping("/result/winner")
    public ResponseEntity<?> findWinner(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 우승자 조회 컨트롤러]", educationId);
        WinnerInfoResponse winner = educationService.findWinner(educationId);
        return ResponseEntity.ok().body(winner);
    }
}
