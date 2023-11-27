package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.generation.AddGenerationRequest;
import cotato.csquiz.domain.dto.generation.ChangePeriodRequest;
import cotato.csquiz.domain.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/generation")
@RequiredArgsConstructor
@Slf4j
public class GenerationController {

    private final GenerationService generationService;

    //기수 추가
    @PostMapping("/add")
    public ResponseEntity<Long> addGeneration(@RequestBody AddGenerationRequest request){
        long generationId = generationService.addGeneration(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(generationId);
    }

    @PatchMapping("/recruiting")
    public ResponseEntity<Long> changeRecruiting(@RequestBody ChangeRecruitingRequest request){
        long generationId = generationService.changeRecruiting(request);
        return ResponseEntity.status(HttpStatus.OK).body(generationId);
    }

    @PatchMapping("/period")
    public ResponseEntity<Long> changePeriod(@RequestBody ChangePeriodRequest request){
        long generationId = generationService.changePeriod(request);
        return ResponseEntity.status(HttpStatus.OK).body(generationId);
    }
}
