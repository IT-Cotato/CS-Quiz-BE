package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.socket.QuizCloseRequest;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.dto.socket.SocketTokenDto;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SocketService {

    private final WebSocketHandler webSocketHandler;
    private final QuizRepository quizRepository;
    private final EducationRepository educationRepository;
    private final KingMemberService kingMemberService;

    private final JwtUtil jwtUtil;

    @Transactional
    public void openCSQuiz(QuizOpenRequest request) {
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.ONGOING);
    }

    @Transactional
    public void accessQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        makeAllStartFalse();
        makeAllStatusFalse();
        quiz.updateStatus(QuizStatus.QUIZ_ON);
        webSocketHandler.accessQuiz(quiz.getId());
    }

    private void checkEducationOpen(Education education) {
        if (EducationStatus.ONGOING != education.getStatus()) {
            throw new AppException(ErrorCode.EDUCATION_CLOSED);
        }
    }

    @Transactional
    public void startQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        isQuizStatusTrue(quiz);
        sleepRandomTime(quiz);
        quiz.updateStart(QuizStatus.QUIZ_ON);
        webSocketHandler.startQuiz(quiz.getId());
    }

    @Transactional
    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(QuizStatus.QUIZ_OFF);
    }

    @Transactional
    public void stopQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        quiz.updateStart(QuizStatus.QUIZ_OFF);
        if (quiz.getNumber() == 9) {
            kingMemberService.calculateKingMember(quiz);
            webSocketHandler.stop9Quiz(quiz.getId());
        }
        if (quiz.getNumber() == 10) {
            kingMemberService.saveWinner(quiz);
            webSocketHandler.stop10Quiz(quiz.getId());
        }
    }

    @Transactional
    public void stopAllQuiz(QuizCloseRequest request) {
        closeAllFlags();
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.FINISHED);
        webSocketHandler.stopAllQuiz(education.getId());
    }

    @Transactional
    public void closeAllFlags() {
        makeAllStatusFalse();
        makeAllStartFalse();
    }

    private void makeAllStatusFalse() {
        quizRepository.findByStatus(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStatus(QuizStatus.QUIZ_OFF));
    }

    private void makeAllStartFalse() {
        quizRepository.findByStart(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStart(QuizStatus.QUIZ_OFF));
    }

    private void isQuizStatusTrue(Quiz quiz) {
        if (quiz.getStatus().equals(QuizStatus.QUIZ_OFF)) {
            throw new AppException(ErrorCode.QUIZ_ACCESS_DENIED);
        }
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈를 찾을 수 없습니다."));
    }

    private Education findEducationById(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
    }

    private void sleepRandomTime(Quiz quiz) {
        try {
            Thread.sleep(1000L * quiz.getAppearSecond());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    public SocketTokenDto createSocketToken(String authorizationHeader) {
        String token = jwtUtil.resolveAccessToken(authorizationHeader);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);
        jwtUtil.validateMemberExist(email);
        String socketToken = jwtUtil.createSocketToken(email, role);
        log.info("[ 소켓 전용 토큰 발급 완료 ]");
        return SocketTokenDto.from(socketToken);
    }
}
