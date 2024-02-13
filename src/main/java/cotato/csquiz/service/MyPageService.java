package cotato.csquiz.service;

import cotato.csquiz.domain.dto.mypage.HallOfFameInfo;
import cotato.csquiz.domain.dto.mypage.HallOfFameResponse;
import cotato.csquiz.domain.dto.mypage.MyHallOfFameInfo;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.RecordRepository;
import cotato.csquiz.repository.ScorerRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageService {

    private final GenerationRepository generationRepository;
    private final QuizRepository quizRepository;
    private final ScorerRepository scorerRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private static final int SHOW_PEOPLE_COUNT = 5;

    public HallOfFameResponse getHallOfFame(Long generationId, String email) {
        Generation generation = generationRepository.findById(generationId).orElseThrow(() ->
                new AppException(ErrorCode.GENERATION_NOT_FOUND)
        );
        List<Quiz> quizzes = quizRepository.findByGeneration(generation);
        List<HallOfFameInfo> scorerHallOfFame = makeScorerHallOfFame(quizzes);
        List<HallOfFameInfo> answerHallOfFame = makeAnswerHallOfFame(quizzes);
        MyHallOfFameInfo myHallOfFameInfo = makeMyHallOfFameInfo(email, quizzes);
        return HallOfFameResponse.from(scorerHallOfFame, answerHallOfFame, myHallOfFameInfo);
    }

    private MyHallOfFameInfo makeMyHallOfFameInfo(String email, List<Quiz> quizzes) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        long scorerCount =  countMyScorer(member,quizzes);
        long answerCount = countMyAnswer(member,quizzes);
        return MyHallOfFameInfo.from(member, scorerCount, answerCount);
    }

    private long countMyScorer(Member member, List<Quiz> quizzes) {
        List<Scorer> memberScorers = quizzes.stream()
                .flatMap(quiz -> scorerRepository.findAllByQuizAndMember(quiz, member).stream())
                .toList();
        return memberScorers.size();
    }

    private long countMyAnswer(Member member, List<Quiz> quizzes) {
        List<Record> memberRecords = quizzes.stream()
                .flatMap(quiz -> recordRepository.findAllByQuizAndIsCorrectAndMember(quiz, true, member).stream())
                .toList();
        return memberRecords.size();
    }

    private List<HallOfFameInfo> makeAnswerHallOfFame(List<Quiz> quizzes) {
        List<Record> recordList = findRecordByQuizzes(quizzes);

        Map<Member, Long> countByMember = recordList.stream()
                .collect(Collectors.groupingBy(Record::getMember, Collectors.counting()));
        List<Entry<Member, Long>> sorted5MemberEntry = sorted5MemberEntry(countByMember);

        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<HallOfFameInfo> makeScorerHallOfFame(List<Quiz> quizzes) {
        List<Scorer> scorerList = findScorerByQuizzes(quizzes);

        Map<Member, Long> countByMember = scorerList.stream()
                .collect(Collectors.groupingBy(Scorer::getMember, Collectors.counting()));
        List<Map.Entry<Member, Long>> sorted5MemberEntry = sorted5MemberEntry(countByMember);

        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<Scorer> findScorerByQuizzes(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> scorerRepository.findAllByQuiz(quiz).stream())
                .toList();
    }

    private List<Record> findRecordByQuizzes(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> recordRepository.findAllByQuizAndIsCorrect(quiz, true).stream())
                .toList();
    }

    private static List<Entry<Member, Long>> sorted5MemberEntry(Map<Member, Long> countByMember) {
        return countByMember.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(SHOW_PEOPLE_COUNT)
                .toList();
    }
}
