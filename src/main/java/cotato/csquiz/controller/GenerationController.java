package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.generation.AddGenerationRequest;
import cotato.csquiz.controller.dto.generation.AddGenerationResponse;
import cotato.csquiz.controller.dto.generation.ChangePeriodRequest;
import cotato.csquiz.controller.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.controller.dto.generation.GenerationInfoResponse;
import cotato.csquiz.service.GenerationService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/generation")
@RequiredArgsConstructor
@Slf4j
public class GenerationController {

    private final GenerationService generationService;

    @GetMapping
    public ResponseEntity<List<GenerationInfoResponse>> getGenerations() {
        return ResponseEntity.ok().body(generationService.getGenerations());
    }

    @PostMapping("/add")
    public ResponseEntity<AddGenerationResponse> addGeneration(@RequestBody @Valid AddGenerationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generationService.addGeneration(request));
    }

    @PatchMapping("/recruiting")
    public ResponseEntity<Void> changeRecruiting(@RequestBody @Valid ChangeRecruitingRequest request) {
        generationService.changeRecruiting(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/period")
    public ResponseEntity<Void> changePeriod(@RequestBody @Valid ChangePeriodRequest request) {
        generationService.changePeriod(request);
        return ResponseEntity.noContent().build();
    }
}
