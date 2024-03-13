package cotato.csquiz.controller;
// TODO :: package 정리
import cotato.csquiz.controller.dto.AllEducationResponse;
import cotato.csquiz.controller.dto.education.AddEducationRequest;
import cotato.csquiz.controller.dto.education.AddEducationResponse;
import cotato.csquiz.controller.dto.education.EducationIdOfQuizResponse;
import cotato.csquiz.controller.dto.education.GetStatusResponse;
import cotato.csquiz.controller.dto.education.UpdateEducationRequest;
import cotato.csquiz.controller.dto.education.WinnerInfoResponse;
import cotato.csquiz.controller.dto.quiz.KingMemberInfo;
import cotato.csquiz.service.EducationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<?> getEducationListByGeneration(@RequestParam(value = "generationId") Long generationId) {
        List<AllEducationResponse> educationList = educationService.getEducationListByGeneration(generationId);
        return ResponseEntity.ok().body(educationList);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestParam(value = "educationId") Long educationId) {
        GetStatusResponse response = educationService.getStatus(educationId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<AddEducationResponse> addEducation(@RequestBody AddEducationRequest request) {
        AddEducationResponse response = educationService.addEducation(request);
        // TODO :: 201 상태코드를 반환하도록 변경
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateEducation(@RequestBody @Valid UpdateEducationRequest request) {
        educationService.updateSubjectAndNumber(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/from")
    public ResponseEntity<?> findEducationId(@RequestParam("quizId") Long quizId) {
        log.info("[{} quizId의 educationId 조회 컨트롤러]", quizId);
        EducationIdOfQuizResponse educationId = educationService.findEducationIdOfQuizId(quizId);
        return ResponseEntity.ok().body(educationId);
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
