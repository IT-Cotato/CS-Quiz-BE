package cotato.csquiz.controller;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.mypage.HallOfFameResponse;
import cotato.csquiz.domain.dto.mypage.MyPageMemberInfoResponse;
import cotato.csquiz.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtUtil jwtUtil;

    @GetMapping("/hall-of-fame")
    public ResponseEntity<?> getHallOfFame(@RequestParam("generationId") Long generationId,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        log.info("교육에 등록된 전체 퀴즈 조회 컨트롤러");
        String email = emailFromAuth(authorizationHeader);
        HallOfFameResponse response = myPageService.getHallOfFame(generationId, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member-info")
    public ResponseEntity<?> userInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String email = emailFromAuth(authorizationHeader);
        MyPageMemberInfoResponse response = myPageService.getMemberInfo(email);
        return ResponseEntity.ok(response);
    }

    private String emailFromAuth(String authorizationHeader) {
        String accessToken = jwtUtil.getBearer(authorizationHeader);
        return jwtUtil.getEmail(accessToken);
    }
}
