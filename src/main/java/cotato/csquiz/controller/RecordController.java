package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.record.RecordsAndScorerResponse;
import cotato.csquiz.domain.dto.record.RegradeRequest;
import cotato.csquiz.domain.dto.record.ReplyRequest;
import cotato.csquiz.domain.dto.record.ReplyResponse;
import cotato.csquiz.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/reply")
    public ResponseEntity<?> replyToQuiz(@RequestBody ReplyRequest request) {
        log.info("정답 제출 컨트롤러, 제출한 정답 : {}", request.input());
        ReplyResponse replyResponse = recordService.replyToQuiz(request);
        return ResponseEntity.ok().body(replyResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRecordsByQuiz(@RequestParam("quizId") Long quizId) {
        log.info("문제에 답한 기록 반환 컨트롤러, 문제 pk: {}", quizId);
        RecordsAndScorerResponse response = recordService.getRecordsAndScorer(quizId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/regrade")
    public ResponseEntity<?> regradeQuiz(@RequestBody RegradeRequest request) {
        log.info("[재채점 컨트롤러] 새로운 정답: {}", request.newAnswer());
        recordService.regradeRecords(request);
        return ResponseEntity.ok().build();
    }
}
