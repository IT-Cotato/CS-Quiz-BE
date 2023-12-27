package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.AddEducationResponse;
import cotato.csquiz.domain.dto.education.GetStatusResponse;
import cotato.csquiz.domain.dto.education.PatchStatusRequest;
import cotato.csquiz.domain.entity.EducationStatus;
import cotato.csquiz.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {
    private final EducationService educationService;

    @GetMapping("/{educationId}/status")
    public ResponseEntity<?> getStatus(@RequestParam(value = "educationId") long educationId) {
        EducationStatus status = educationService.getStatus(educationId);
        GetStatusResponse response = GetStatusResponse.builder()
                .status(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEducation(@RequestBody AddEducationRequest request) {
        long educationId = educationService.addEducation(request);
        AddEducationResponse response = AddEducationResponse.builder()
                .educationId(educationId)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
