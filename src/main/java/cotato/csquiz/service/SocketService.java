package cotato.csquiz.service;

import cotato.csquiz.domain.dto.socket.QuizCloseRequest;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.EducationStatus;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.EducationRepository;
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

    private final EducationRepository educationRepository;

    public void openCSQuiz(QuizOpenRequest request) {
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.ONGOING);
    }

    public void accessQuiz(QuizSocketRequest request) {
        makeAllStatusFalse();
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(QuizStatus.QUIZ_ON);
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
        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(false);
    }

    public void stopQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStart(false);
    }

    public void stopAllQuiz(QuizCloseRequest request) {
        makeAllStatusFalse();
        makeAllStartFalse();
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.CLOSED);
    }

    private void makeAllStatusFalse() {
        quizRepository.findByStatus(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStatus(QuizStatus.QUIZ_OFF));
    }

    private void makeAllStartFalse() {
        quizRepository.findByStart(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStart(false));
    }

    private void isQuizStatusTrue(Quiz quiz) {
        if (quiz.getStatus().equals(QuizStatus.QUIZ_OFF)) {
            throw new AppException(ErrorCode.QUIZ_ACCESS_DENIED);
        }
    }

    private Quiz findQuizById(long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOTFOUND));
    }

    private Education findEducationById(long educationId) {
        return educationRepository.findById(educationId).orElseThrow(
                () -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
    }
}
