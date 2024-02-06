package cotato.csquiz.service;

import cotato.csquiz.domain.dto.record.ReplyRequest;
import cotato.csquiz.domain.dto.record.ReplyResponse;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.RecordRepository;
import cotato.csquiz.repository.ScorerRepository;
import cotato.csquiz.utils.QuizAnswerRedisRepository;
import cotato.csquiz.utils.ScorerExistRedisRepository;
import cotato.csquiz.utils.TicketCountRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final ScorerRepository scorerRepository;
    private final QuizAnswerRedisRepository quizAnswerRedisRepository;
    private final TicketCountRedisRepository ticketCountRedisRepository;
    private final ScorerExistRedisRepository scorerExistRedisRepository;

    @Transactional
    public ReplyResponse replyToQuiz(ReplyRequest request) {
        Quiz findQuiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        validateQuizOpen(findQuiz);
        Member findMember = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        boolean isCorrect = quizAnswerRedisRepository.isCorrect(findQuiz, request.input());
        Long ticketNumber = ticketCountRedisRepository.increment(findQuiz.getId());
        if (isCorrect && !scorerExistRedisRepository.isExist(findQuiz)) {
            scorerExistRedisRepository.saveScorer(findQuiz);
            Scorer scorer = Scorer.of(findMember, findQuiz);
            log.info("득점자 생성 : {}, 티켓번호: {}", findMember.getId(), ticketNumber);
            scorerRepository.save(scorer);
        }
        Record createdRecord = Record.of(request.input(), isCorrect, findMember, findQuiz, ticketNumber);
        recordRepository.save(createdRecord);
        return ReplyResponse.from(isCorrect);
    }

    private void validateQuizOpen(Quiz findQuiz) {
        if (findQuiz.isOff() || !findQuiz.isStart()) {
            throw new AppException(ErrorCode.QUIZ_ACCESS_DENIED);
        }
    }

    @Transactional
    public void saveAnswers(QuizOpenRequest request) {
        scorerExistRedisRepository.saveAllScorerNone(request.getEducationId());
        quizAnswerRedisRepository.saveAllQuizAnswers(request.getEducationId());
    }
}