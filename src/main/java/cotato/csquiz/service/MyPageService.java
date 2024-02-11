package cotato.csquiz.service;

import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.RecordRepository;
import cotato.csquiz.repository.ScorerRepository;
import java.util.List;
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

    public void getHallOfFame(Long generationId) {
        Generation generation = generationRepository.findById(generationId).orElseThrow(() ->
                new AppException(ErrorCode.GENERATION_NOT_FOUND)
        );
        List<Quiz> quizzes = quizRepository.findByGeneration(generation);
        List<Scorer> scorerList = findScorerByQuiz(quizzes);
        List<Record> recordList = findRecordByQuiz(quizzes);
    }

    private List<Scorer> findScorerByQuiz(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> scorerRepository.findAllByQuiz(quiz).stream())
                .collect(Collectors.toList());
    }

    private List<Record> findRecordByQuiz(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(quiz -> recordRepository.findAllByQuiz(quiz).stream())
                .collect(Collectors.toList());
    }
}
