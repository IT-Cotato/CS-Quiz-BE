package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.socket.AccessQuizRequest;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SocketService {

    private final WebSocketHandler webSocketHandler;

    private final QuizRepository quizRepository;

    public void accessQuiz(AccessQuizRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(true);
        webSocketHandler.accessQuiz(quiz.getId());
    }

    private Quiz findQuizById(long quizId) {
        return quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
    }
}
