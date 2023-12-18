package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.session.*;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    //세션 추가
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addSession(@ModelAttribute AddSessionRequest request) throws ImageException {
        log.info(request.getDescription());
        long sessionId = sessionService.addSession(request);
        AddSessionResponse response = AddSessionResponse.builder()
                .sessionId(sessionId).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/sessionNum")
    public ResponseEntity<?> changeSessionNum(@RequestBody SessionNumRequest request){
        long sessionNum = sessionService.changeSessionNum(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<?> changeContent(@RequestBody SessionDescriptionRequest request){
        long sessionId = sessionService.changeDescription(request);
        return ResponseEntity.ok().build();
    }
    @PatchMapping(value = "/photoUrl", consumes = "multipart/form-data")
    public ResponseEntity<?> changePhotoUrl(@ModelAttribute SessionPhotoUrlRequest request) throws ImageException{
        long sessionId = sessionService.changePhotoUrl(request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{generationId}/sessions")
    public ResponseEntity<List<Session>> getSessions(@PathVariable long generationId){
        List<Session> sessions = sessionService.getSessionsByGenerationId(generationId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
