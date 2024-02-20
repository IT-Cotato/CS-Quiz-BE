package cotato.csquiz.service;

import cotato.csquiz.domain.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.domain.dto.record.RecordResponse;
import cotato.csquiz.domain.dto.record.RecordsAndScorerResponse;
import cotato.csquiz.domain.dto.record.RegradeRequest;
import cotato.csquiz.domain.dto.record.ReplyRequest;
import cotato.csquiz.domain.dto.record.ReplyResponse;
import cotato.csquiz.domain.dto.record.ScorerResponse;
import cotato.csquiz.domain.dto.socket.QuizOpenRequest;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MultipleQuiz;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        Quiz findQuiz = findQuizById(request.quizId());
        validateQuizOpen(findQuiz);
        Member findMember = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
//        validateAlreadyCorrect(findQuiz, findMember);
        boolean isCorrect = quizAnswerRedisRepository.isCorrect(findQuiz, request.input());
        Long ticketNumber = ticketCountRedisRepository.increment(findQuiz.getId());
        if (isCorrect && scorerExistRedisRepository.isNotExist(findQuiz)) {
            scorerExistRedisRepository.saveScorer(findQuiz, ticketNumber);
            Scorer scorer = Scorer.of(findMember, findQuiz);
            log.info("득점자 생성 : {}, 티켓번호: {}", findMember.getId(), ticketNumber);
            scorerRepository.save(scorer);
        }
        Record createdRecord = Record.of(request.input(), isCorrect, findMember, findQuiz, ticketNumber);
        recordRepository.save(createdRecord);
        return ReplyResponse.from(isCorrect);
    }

    private void validateAlreadyCorrect(Quiz findQuiz, Member findMember) {
        if (recordRepository.findByQuizAndMemberAndIsCorrect(findQuiz, findMember, true).isPresent()) {
            log.warn("이미 해당 문제에 정답 제출한 사용자입니다.");
            log.warn("문제 번호: {}, 제출한 멤버: {}", findQuiz.getNumber(), findMember.getName());
            throw new AppException(ErrorCode.ALREADY_REPLY_CORRECT);
        }
    }

    @Transactional
    public void addAdditionalAnswerToRedis(AddAdditionalAnswerRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        quizAnswerRedisRepository.saveAdditionalQuizAnswer(quiz, request.getAnswer());
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId).orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
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

    @Transactional
    public void regradeRecords(RegradeRequest request) {
        Quiz quiz = findQuizById(request.quizId());
        validateQuizType(quiz);
        List<Record> correctRecords = recordRepository.findAllByQuizAndReply(quiz, request.newAnswer());
        correctRecords.forEach(record -> record.changeCorrect(true));
        recordRepository.saveAll(correctRecords);
        Record fastestRecord = correctRecords.stream()
                .min(Comparator.comparing(Record::getTicketNumber))
                .orElseThrow(() -> new AppException(ErrorCode.REGRADE_FAIL));
        scorerRepository.findByQuiz(quiz)
                .ifPresentOrElse(
                        scorer -> changeScorer(scorer, fastestRecord),
                        () -> createScorer(fastestRecord)
                );
    }

    private void validateQuizType(Quiz quiz) {
        if (quiz instanceof MultipleQuiz) {
            throw new AppException(ErrorCode.QUIZ_TYPE_NOT_MATCH);
        }
    }

    private void changeScorer(Scorer previousScorer, Record fastestRecord) {
        if (isFaster(previousScorer, fastestRecord)) {
            log.info("[득점자 변경] 새로운 티켓 번호: {}", fastestRecord.getTicketNumber());
            Scorer changedScorer = previousScorer.changeMember(fastestRecord.getMember());
            scorerRepository.save(changedScorer);
        }
    }

    private boolean isFaster(Scorer previousScorer, Record fastestRecord) {
        return scorerExistRedisRepository.getScorerTicketNumber(previousScorer.getQuiz())
                > fastestRecord.getTicketNumber();
    }

    private void createScorer(Record fastestRecord) {
        Scorer scorer = Scorer.of(fastestRecord.getMember(), fastestRecord.getQuiz());
        scorerRepository.save(scorer);
        scorerExistRedisRepository.saveScorer(fastestRecord.getQuiz(), fastestRecord.getTicketNumber());
    }

    @Transactional
    public RecordsAndScorerResponse getRecordsAndScorer(Long quizId) {
        Quiz findQuiz = findQuizById(quizId);
        List<RecordResponse> records = getRecordByQuiz(findQuiz);
        Optional<Scorer> scorer = scorerRepository.findByQuiz(findQuiz);
        if (scorer.isPresent()) {
            log.info("[기존 득점자 존재]: {}", scorer.get().getMember().getName());
            return RecordsAndScorerResponse.from(records,
                    ScorerResponse.from(scorer.get()));
        }
        log.info("[응답과 득점자 반환 서비스]");
        return RecordsAndScorerResponse.from(records, null);
    }

    private List<RecordResponse> getRecordByQuiz(Quiz quiz) {
        List<Record> records = recordRepository.findAllByQuiz(quiz);
        log.info("[문제에 모든 응답 반환 서비스]");
        return records.stream()
                .sorted(Comparator.comparing(Record::getTicketNumber))
                .map(RecordResponse::from)
                .toList();
    }
}
