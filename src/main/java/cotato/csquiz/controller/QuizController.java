package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.quiz.AllQuizzesResponse;
import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping(value = "/adds", consumes = "multipart/form-data")
    public ResponseEntity<?> addAllQuizzes(@ModelAttribute CreateQuizzesRequest request,
                                           @RequestParam("educationId") Long educationId) {
        log.info("퀴즈 등록 컨트롤러, 요청 개수");
        quizService.createQuizzes(educationId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllQuizzes(@RequestParam("educationId") Long educationId) {
        log.info("교육에 등록된 전체 퀴즈 조회 컨트롤러");
        AllQuizzesResponse allQuizzes = quizService.getAllQuizzes(educationId);
        return ResponseEntity.ok(allQuizzes);
    }
}
