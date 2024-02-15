package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.domain.dto.quiz.AllQuizzesInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.AllQuizzesResponse;
import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.domain.dto.quiz.QuizInfoInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizResultsResponse;
import cotato.csquiz.service.QuizService;
import cotato.csquiz.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping
    public ResponseEntity<?> getOneQuiz(@RequestParam("quizId") Long quizId) {
        log.info("특정 퀴즈 반환 컨트롤러");
        QuizResponse response = quizService.getQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cs-admin/all")
    public ResponseEntity<?> getAllQuizzesInCsQuiz(@RequestParam("educationId") Long educationId) {
        log.info("특정 세션 관리자용 문제 반환 컨트롤러");
        AllQuizzesInCsQuizResponse allQuizzes = quizService.getAllQuizzesInCsQuiz(educationId);
        return ResponseEntity.ok(allQuizzes);
    }

    @GetMapping("/cs-admin")
    public ResponseEntity<?> getQuizInCsQuiz(@RequestParam("quizId") Long quizId) {
        log.info("관리자용 한 문제 조회 컨트롤러");
        QuizInfoInCsQuizResponse response = quizService.getQuizInCsQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cs-admin/answer/add")
    public ResponseEntity<?> addAnswer(@RequestBody AddAdditionalAnswerRequest request) {
        log.info("정답 추가에 따른 재채점 컨트롤러");
        quizService.addAdditionalAnswer(request);
        recordService.addAdditionalAnswerToRedis(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cs-admin/results")
    public ResponseEntity<?> quizResults(@RequestParam("educationId") Long educationId) {
        log.info("cs문제 풀이의 득점자 목록 조회 컨트롤러");
        QuizResultsResponse response = quizService.findQuizResults(educationId);
        return ResponseEntity.ok(response);
    }
}
