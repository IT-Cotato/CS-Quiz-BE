package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.socket.QuizCloseRequest;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.dto.socket.SocketTokenDto;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import java.util.List;
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

    private final MemberRepository memberRepository;

    private final QuizService quizService;

    private final JwtUtil jwtUtil;

    @Transactional
    public void openCSQuiz(QuizOpenRequest request) {
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.ONGOING);
    }

    @Transactional
    public void accessQuiz(QuizSocketRequest request) {
        makeAllStartFalse();
        makeAllStatusFalse();
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(QuizStatus.QUIZ_ON);
        webSocketHandler.accessQuiz(quiz.getId());
    }

    @Transactional
    public void startQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        isQuizStatusTrue(quiz);
        quiz.updateStart(true);
        sleepRandomTime(quiz);
        webSocketHandler.startQuiz(quiz.getId());
    }

    @Transactional
    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(false);
    }

    @Transactional
    public void stopQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quiz.updateStart(false);
        calculateKingMember(quiz);
    }

    private void calculateKingMember(Quiz quiz) {
        if (quiz.getNumber() == 9) {
            log.info("9번 문제 CS퀴즈 우승자 결정");
            Long educationId = findEducationIdByQuiz(quiz);
            List<Member> kingMembers = quizService.findKingMember(educationId);
            //킹킹문제 DB에 kingMembers 멤버들 저장 TODO
            if (kingMembers.size() == 1) {
                quizService.saveWinner(kingMembers.get(0), educationId); //1명이면 우승자도 저장
            }
        }
        if (quiz.getNumber() == 10) {
            log.info("10번 문제 CS퀴즈 우승자 결정");
            //education에 우승자가 있는지 확인 TODO
            //있으면 끝 없으면 10번째 문제 정답자 찾기 TODO
            quizService.saveWinner(/*멤버Id 넣기*/);
        }
    }

    @Transactional
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

    private void sleepRandomTime(Quiz quiz) {
        try {
            Thread.sleep(1000L * quiz.getAppearSecond());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public SocketTokenDto createSocketToken(String authorizationHeader) {
        String token = jwtUtil.resolveWithAccessToken(authorizationHeader);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);
        jwtUtil.validateMemberExist(email);
        String socketToken = jwtUtil.createSocketToken(email, role);
        log.info("[ 소켓 전용 토큰 발급 완료 ]");
        return SocketTokenDto.of(socketToken);
    }

    private Long findEducationIdByQuiz(Quiz quiz) {
        return quiz.getEducation().getId();
    }
}
