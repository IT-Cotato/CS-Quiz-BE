package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.socket.QuizCloseRequest;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.dto.socket.SocketTokenDto;
import cotato.csquiz.service.RecordService;
import cotato.csquiz.service.SocketService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;
    private final RecordService recordService;

    @PatchMapping("/start/csquiz")
    public ResponseEntity<?> openCSQuiz(@RequestBody QuizOpenRequest request) {
        socketService.openCSQuiz(request);
        recordService.saveAnswers(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/access")
    public ResponseEntity<?> accessQuiz(@RequestBody QuizSocketRequest request) {
        socketService.accessQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/start")
    public ResponseEntity<?> startQuiz(@RequestBody QuizSocketRequest request) {
        socketService.startQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deny")
    public ResponseEntity<?> denyQuiz(@RequestBody QuizSocketRequest request) {
        socketService.denyQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/stop")
    public ResponseEntity<?> stopQuiz(@RequestBody QuizSocketRequest request) {
        socketService.stopQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/close/csquiz")
    public ResponseEntity<?> stopAllQuiz(@RequestBody QuizCloseRequest request) {
        socketService.stopAllQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token")
    public ResponseEntity<?> makeSocketToken(@RequestHeader("Authorization") String authorizationHeader) {
        SocketTokenDto response = socketService.createSocketToken(authorizationHeader);
        return ResponseEntity.ok(response);
    }
}
