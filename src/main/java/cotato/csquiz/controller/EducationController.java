package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.EducationDto;
import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.AddEducationResponse;
import cotato.csquiz.domain.dto.education.GetStatusResponse;
import cotato.csquiz.domain.dto.education.PatchEducationRequest;
import cotato.csquiz.domain.dto.education.PatchStatusRequest;
import cotato.csquiz.domain.dto.education.PatchSubjectRequest;
import cotato.csquiz.domain.entity.EducationStatus;
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
    public ResponseEntity<?> getStatus(@RequestParam(value = "educationId") long educationId) {
        EducationStatus status = educationService.getStatus(educationId);
        GetStatusResponse response = GetStatusResponse.builder()
                .status(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<AddEducationResponse> addEducation(@RequestBody AddEducationRequest request) {
        AddEducationResponse response = educationService.addEducation(request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<?> patchStatus(@RequestBody PatchStatusRequest request) {
        educationService.patchStatus(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/subject")
    public ResponseEntity<?> patchSubject(@RequestBody PatchSubjectRequest request) {
        educationService.patchSubject(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getEducationListByGeneration(@RequestParam(value = "generationId") long generationId) {
        List<EducationDto> educationList = educationService.getEducationListByGeneration(generationId);
        return ResponseEntity.ok().body(educationList);
    }

    @PatchMapping("/status")
    public ResponseEntity<?> patchEducationStatus(@RequestBody PatchEducationRequest request) {
        educationService.patchEducationStatus(request);
        return ResponseEntity.ok().build();
    }
}
