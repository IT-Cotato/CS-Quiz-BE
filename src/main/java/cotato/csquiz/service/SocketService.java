package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.socket.QuizCloseRequest;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.dto.socket.QuizSocketRequest;
import cotato.csquiz.domain.dto.socket.SocketTokenDto;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.KingMemberRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ScorerRepository;
import cotato.csquiz.repository.WinnerRepository;
import java.util.List;
import java.util.Optional;
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

    private final KingMemberRepository kingMemberRepository;

    private final WinnerRepository winnerRepository;

    private final ScorerRepository scorerRepository;

    private final QuizService quizService;

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
        if (EducationStatus.CLOSED == education.getStatus()) {
            throw new AppException(ErrorCode.EDUCATION_CLOSED);
        }
    }

    @Transactional
    public void startQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        isQuizStatusTrue(quiz);
        quiz.updateStart(true);
        sleepRandomTime(quiz);
        webSocketHandler.startQuiz(quiz.getId());
    }

    @Transactional
    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(false);
    }

    @Transactional
    public void stopQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        checkEducationOpen(quiz.getEducation());
        quiz.updateStart(false);
        calculateKingMember(quiz);
    }

    private void calculateKingMember(Quiz quiz) {
        Education education = findEducationByQuiz(quiz);

        if (quiz.getNumber() == 9) {
            decideWinnerForQuestionNine(education);
        }

        if (quiz.getNumber() == 10) {
            decideWinnerForQuestionTen(education, quiz);
        }
    }

    private void decideWinnerForQuestionNine(Education education) {
        log.info("9번 문제 CS퀴즈 우승자 결정");
        List<Member> kingMembers = quizService.findKingMember(education.getId());
        saveKingMembers(kingMembers, education);
        if (kingMembers.size() == 1) {
            winnerRepository.save(Winner.of(kingMembers.get(0), education));
        }
    }

    private void decideWinnerForQuestionTen(Education education, Quiz quiz) {
        log.info("10번 문제 CS퀴즈 우승자 결정");
        if (winnerRepository.findByEducation(education).isEmpty()) {
            Scorer scorer = scorerRepository.findByQuiz(quiz).orElseThrow(() ->
                    new AppException(ErrorCode.SCORER_NOT_FOUND));
            Member winnerMember = scorer.getMember();
            winnerRepository.save(Winner.of(winnerMember, education));
            log.info("우승자 등록: " + winnerMember);
        }
    }

    private List<KingMember> saveKingMembers(List<Member> kingMembers, Education education) {
        return kingMembers.stream()
                .map(kingMember -> saveKingMember(education, kingMember))
                .toList();
    }


    @Transactional
    public void stopAllQuiz(QuizCloseRequest request) {
        closeAllFlags();
        Education education = findEducationById(request.getEducationId());
        education.changeStatus(EducationStatus.CLOSED);
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

    private Education findEducationByQuiz(Quiz quiz) {
        return quiz.getEducation();
    }

    private KingMember saveKingMember(Education education, Member kingMember) {
        return kingMemberRepository.save(KingMember.of(kingMember, education));
    }
}
