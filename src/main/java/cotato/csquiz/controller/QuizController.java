package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.domain.dto.quiz.AllQuizzesInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.AllQuizzesResponse;
import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.domain.dto.quiz.QuizInfoInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizResultInfo;
import cotato.csquiz.service.QuizService;
import cotato.csquiz.service.RecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final RecordService recordService;

    @PostMapping(value = "/adds", consumes = "multipart/form-data")
    public ResponseEntity<?> addAllQuizzes(@ModelAttribute CreateQuizzesRequest request,
                                           @RequestParam("educationId") Long educationId) {
        quizService.createQuizzes(educationId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllQuizzesForEducationTeam(@RequestParam("educationId") Long educationId) {
        AllQuizzesResponse allQuizzes = quizService.findAllQuizzesForEducationTeam(educationId);
        return ResponseEntity.ok(allQuizzes);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> findOneQuizForMember(@PathVariable("quizId") Long quizId) {
        QuizResponse response = quizService.findOneQuizForMember(quizId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/cs-admin/all")
    public ResponseEntity<?> getAllQuizzesInCsQuiz(@RequestParam("educationId") Long educationId) {
        AllQuizzesInCsQuizResponse allQuizzes = quizService.getAllQuizzesInCsQuiz(educationId);
        return ResponseEntity.ok(allQuizzes);
    }

    @GetMapping("/cs-admin")
    public ResponseEntity<?> getQuizInCsQuiz(@RequestParam("quizId") Long quizId) {
        QuizInfoInCsQuizResponse response = quizService.getQuizInCsQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cs-admin/answer/add")
    public ResponseEntity<?> addAnswer(@RequestBody AddAdditionalAnswerRequest request) {
        quizService.addAdditionalAnswer(request);
        recordService.addAdditionalAnswerToRedis(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cs-admin/results")
    public ResponseEntity<?> quizResults(@RequestParam("educationId") Long educationId) {
        List<QuizResultInfo> response = quizService.findQuizResults(educationId);
        return ResponseEntity.ok(response);
    }
}
