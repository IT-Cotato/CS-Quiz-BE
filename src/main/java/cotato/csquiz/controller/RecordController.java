package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.record.ReplyRequest;
import cotato.csquiz.domain.dto.record.ReplyResponse;
import cotato.csquiz.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
