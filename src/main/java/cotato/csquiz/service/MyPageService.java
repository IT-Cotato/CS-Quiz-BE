package cotato.csquiz.service;

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
        List<Scorer> scorerList = findScorerByQuizzes(quizzes);
        Map<Member, Long> countByMember = scorerList.stream()
                .collect(Collectors.groupingBy(Scorer::getMember, Collectors.counting()));
        List<Map.Entry<Member,Long>> sorted5Member = countByMember.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList()
                .subList(0,SHOW_PEOPLE_COUNT);
        for (Map.Entry<Member, Long> countData : sorted5Member) {
            Member member = countData.getKey();
        }
        List<Record> recordList = findRecordByQuizzes(quizzes);
    }

    private List<Scorer> findScorerByQuizzes(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> scorerRepository.findAllByQuiz(quiz).stream())
                .collect(Collectors.toList());
    }

    private List<Record> findRecordByQuizzes(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> recordRepository.findAllByQuiz(quiz).stream())
                .collect(Collectors.toList());
    }
}
