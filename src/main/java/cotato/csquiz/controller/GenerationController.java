package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.generation.AddGenerationRequest;
import cotato.csquiz.domain.dto.generation.AddGenerationResponse;
import cotato.csquiz.domain.dto.generation.ChangePeriodRequest;
import cotato.csquiz.domain.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/generation")
@RequiredArgsConstructor
@Slf4j
public class GenerationController {

    private final GenerationService generationService;

    @GetMapping("")
    public ResponseEntity<?> getGenerations() {
        return ResponseEntity.ok().body(generationService.getGenerations());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGeneration(@RequestBody AddGenerationRequest request) {
        AddGenerationResponse response = generationService.addGeneration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/recruiting")
    public ResponseEntity<?> changeRecruiting(@RequestBody ChangeRecruitingRequest request) {
        generationService.changeRecruiting(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/period")
    public ResponseEntity<?> changePeriod(@RequestBody ChangePeriodRequest request) {
        generationService.changePeriod(request);
        return ResponseEntity.ok().build();
    }
}
