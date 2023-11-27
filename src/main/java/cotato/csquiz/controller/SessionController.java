package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.entity.Session;
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
    @PostMapping("/add")
    public ResponseEntity<Long> addSession(@RequestBody AddSessionRequest request){
        long sessionId = sessionService.addSession(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }

    @PatchMapping("/sessionNum")
    public ResponseEntity<Long> changeSessionNum(@RequestBody SessionNumRequest request){
        long sessionNum = sessionService.changeSessionNum(request);
        return ResponseEntity.status(HttpStatus.OK).body(sessionNum);
    }

    @PatchMapping("/description")
    public ResponseEntity<Long> changeContent(@RequestBody SessionDescriptionRequest request){
        long sessionId = sessionService.changeDescription(request);
        return ResponseEntity.status(HttpStatus.OK).body(sessionId);
    }
    @PatchMapping("/photoUrl")
    public ResponseEntity<Long> changePhotoUrl(@RequestBody SessionPhotoUrlRequest request){
        long sessionId = sessionService.changePhotoUrl(request);
        return ResponseEntity.status(HttpStatus.OK).body(sessionId);
    }
    @GetMapping("/{generationId}/sessions")
    public ResponseEntity<List<Session>> getSessions(@PathVariable long generationId){
        List<Session> sessions = sessionService.getSessionsByGenerationId(generationId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
