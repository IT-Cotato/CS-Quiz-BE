package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.AddSessionResponse;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.service.SessionService;
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
    public ResponseEntity<List<Session>> getSessions(@RequestParam long generationId) {
        List<Session> sessions = sessionService.findSessionsByGenerationId(generationId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addSession(@ModelAttribute AddSessionRequest request) throws ImageException {
        log.info("세션 추가 컨트롤러 : {}", request.getDescription());
        AddSessionResponse response = sessionService.addSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/number")
    public ResponseEntity<?> changeSessionNum(@RequestBody SessionNumRequest request) {
        sessionService.changeSessionNum(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<?> changeContent(@RequestBody SessionDescriptionRequest request) {
        sessionService.changeDescription(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/update/photo", consumes = "multipart/form-data")
    public ResponseEntity<?> changePhotoUrl(@ModelAttribute SessionPhotoUrlRequest request) throws ImageException {

        sessionService.changePhotoUrl(request);
        return ResponseEntity.ok().build();
    }
}
