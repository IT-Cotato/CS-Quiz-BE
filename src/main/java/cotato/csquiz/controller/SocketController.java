package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;

    //문제 접근 허용
    @PostMapping("/access")
    public ResponseEntity<?> accessQuiz(@RequestBody QuizSocketRequest request) {
        socketService.accessQuiz(request);
        return ResponseEntity.ok().build();
    }

    //퀴즈 풀기 시작
    @PostMapping("/start")
    public ResponseEntity<?> startQuiz(@RequestBody QuizSocketRequest request) {
        socketService.startQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deny")
    //문제 접근 차단
    public ResponseEntity<?> denyQuiz(@RequestBody QuizSocketRequest request) {
        socketService.denyQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    //퀴즈 풀기 닫기
    public ResponseEntity<?> stopQuiz(@RequestBody QuizSocketRequest request) {
        socketService.stopQuiz(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stopAll")
    public ResponseEntity<?> stopAllQuiz() {
        socketService.stopAllQuiz();
        return ResponseEntity.ok().build();
    }
}
