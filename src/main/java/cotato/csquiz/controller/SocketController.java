package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.socket.AccessQuizRequest;
import cotato.csquiz.domain.dto.socket.StartQuizRequest;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.SocketHandler;

@RestController
@RequestMapping("/v1/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;

    //문제 접근 허용
    @PostMapping("/access")
    public ResponseEntity<?> accessQuiz(@RequestBody AccessQuizRequest request) {
        socketService.accessQuiz(request);
        return ResponseEntity.ok().build();
    }
    //퀴즈 풀기 시작
    @PostMapping("/start")
    public ResponseEntity<?> startQuiz(@RequestBody StartQuizRequest request) {
        socketService.startQuiz(request);
        return ResponseEntity.ok().build();
    }
}
