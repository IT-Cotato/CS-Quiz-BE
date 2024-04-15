package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.session.AddSessionRequest;
import cotato.csquiz.controller.dto.session.AddSessionResponse;
import cotato.csquiz.controller.dto.session.CsEducationOnSessionNumberResponse;
import cotato.csquiz.controller.dto.session.SessionDescriptionRequest;
import cotato.csquiz.controller.dto.session.SessionListResponse;
import cotato.csquiz.controller.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.controller.dto.session.UpdateSessionNumberRequest;
import cotato.csquiz.controller.dto.session.UpdateSessionRequest;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("")
    public ResponseEntity<List<SessionListResponse>> getSessions(@RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.findSessionsByGenerationId(generationId));
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<AddSessionResponse> addSession(@ModelAttribute @Valid AddSessionRequest request)
            throws ImageException {
        log.info("세션 추가 컨트롤러 : {}", request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.addSession(request));
    }

    @PatchMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateSession(@ModelAttribute @Valid UpdateSessionRequest request)
            throws ImageException {
        sessionService.updateSession(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/number")
    public ResponseEntity<Void> changeSessionNum(@RequestBody @Valid UpdateSessionNumberRequest request) {
        sessionService.changeSessionNumber(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> changeContent(@RequestBody @Valid SessionDescriptionRequest request) {
        sessionService.changeDescription(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/update/photo", consumes = "multipart/form-data")
    public ResponseEntity<Void> changePhotoUrl(@ModelAttribute @Valid SessionPhotoUrlRequest request)
            throws ImageException {
        sessionService.changePhotoUrl(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cs-on")
    public ResponseEntity<List<CsEducationOnSessionNumberResponse>> getSessionsWithCsEducationOn(
            @RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(sessionService.findAllCsOnSessionsByGenerationId(generationId));
    }
}
