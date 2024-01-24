package cotato.csquiz.service;

import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.QuizStatus;
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

    public void accessQuiz(QuizSocketRequest request) {
        makeAllStatusFalse();
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(true);
        webSocketHandler.accessQuiz(quiz.getId());
    }

    public void startQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        isQuizStatusTrue(quiz);
        quiz.updateStart(true);
        webSocketHandler.startQuiz(quiz.getId());
    }

    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(false);
        quiz.updateStart(false);
    }

    public void stopQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStart(false);
    }

    public void stopAllQuiz() {
        makeAllStatusFalse();
        makeAllStartFalse();
    }

    private void makeAllStatusFalse() {
        quizRepository.findByStatus(QuizStatus.ON)
                .forEach(quiz -> quiz.updateStatus(false));
    }

    private void makeAllStartFalse() {
        quizRepository.findByStart(QuizStatus.ON)
                .forEach(quiz -> quiz.updateStart(false));
    }

    private void isQuizStatusTrue(Quiz quiz) {
        if (quiz.getStatus().equals(QuizStatus.OFF)) {
            throw new AppException(ErrorCode.Quiz_OFF);
        }
    }

    private Quiz findQuizById(long quizId) {
        return quizRepository.findById(quizId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
    }
}
