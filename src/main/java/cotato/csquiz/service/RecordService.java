package cotato.csquiz.service;

import cotato.csquiz.domain.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.domain.dto.record.RecordResponse;
import cotato.csquiz.domain.dto.record.RecordsAndScorerResponse;
import cotato.csquiz.domain.dto.record.ReplyRequest;
import cotato.csquiz.domain.dto.record.ReplyResponse;
import cotato.csquiz.domain.dto.record.ScorerResponse;
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
        validateAlreadyCorrect(findQuiz, findMember);
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
    public void reGradeRecords(AddAdditionalAnswerRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        List<Record> records = recordRepository.findAllByQuizAndReply(quiz, request.getAnswer());
        records.forEach(record -> record.changeCorrect(true));
        Record fastestRecord = records.stream()
                .min(Comparator.comparing(Record::getTicketNumber))
                .orElse(null);
        Scorer previousFastestScorer = scorerRepository.findByQuiz(quiz);
        recordRepository.saveAll(records);
        if (fastestRecord != null) {
            changeScorer(fastestRecord, previousFastestScorer);
        }
    }

    private void changeScorer(Record fastestRecord, Scorer previousFastestScorer) {
        if (shouldChangeScorer(fastestRecord, previousFastestScorer)) {
            Scorer newScorer = (previousFastestScorer == null)
                    ? createScorer(fastestRecord)
                    : changeScorerMember(previousFastestScorer, fastestRecord.getMember());
            scorerRepository.save(newScorer);
        }
    }

    private boolean shouldChangeScorer(Record fastestRecord, Scorer previousFastestScorer) {
        return previousFastestScorer == null
                || isFastestRecord(fastestRecord, previousFastestScorer);
    }

    private boolean isFastestRecord(Record fastestRecord, Scorer previousFastestScorer) {
        Record previousFastestRecord = findPreviousFastestRecord(previousFastestScorer);
        return previousFastestRecord == null
                || fastestRecord.getTicketNumber() < previousFastestRecord.getTicketNumber();
    }

    private Record findPreviousFastestRecord(Scorer previousFastestScorer) {
        List<Record> previousRecords = recordRepository.findAllByQuizAndMemberAndIsCorrect(
                previousFastestScorer.getQuiz(), previousFastestScorer.getMember(), true);
        return previousRecords.stream()
                .min(Comparator.comparingLong(Record::getTicketNumber))
                .orElse(null);
    }

    private Scorer createScorer(Record fastestRecord) {
        return Scorer.of(fastestRecord.getMember(), fastestRecord.getQuiz());
    }

    private Scorer changeScorerMember(Scorer previousFastestScorer, Member member) {
        return previousFastestScorer.changeMember(member);
    }

    @Transactional
    public RecordsAndScorerResponse getRecordsAndScorer(Long quizId) {
        Quiz findQuiz = findQuizById(quizId);
        List<RecordResponse> records = getRecordByQuiz(findQuiz);
        Optional<Scorer> scorer = scorerRepository.findByQuiz(findQuiz);
        if (scorer.isPresent()) {
            log.info("[기존 득점자 존재]");
            return RecordsAndScorerResponse.from(records,
                    ScorerResponse.from(scorerRepository.findByQuiz(findQuiz).get()));
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
