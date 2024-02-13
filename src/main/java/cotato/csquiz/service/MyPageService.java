package cotato.csquiz.service;

import cotato.csquiz.domain.dto.mypage.HallOfFameInfo;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
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
    private static final int SHOW_PEOPLE_COUNT = 5;

    public void getHallOfFame(Long generationId) {
        Generation generation = generationRepository.findById(generationId).orElseThrow(() ->
                new AppException(ErrorCode.GENERATION_NOT_FOUND)
        );
        List<Quiz> quizzes = quizRepository.findByGeneration(generation);
        List<HallOfFameInfo> scorerHallOfFame = makeScorerHallOfFame(quizzes);
        List<HallOfFameInfo> answerHallOfFame = makeAnswerHallOfFame(quizzes);
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
                .collect(Collectors.toList());
    }

    private List<Record> findRecordByQuizzes(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> recordRepository.findAllByQuizAndIsCorrect(quiz, true).stream())
                .collect(Collectors.toList());
    }

    private static List<Entry<Member, Long>> sorted5MemberEntry(Map<Member, Long> countByMember) {
        return countByMember.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(SHOW_PEOPLE_COUNT)
                .toList();
    }
}
