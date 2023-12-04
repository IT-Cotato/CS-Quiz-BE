package cotato.csquiz.controller;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.dto.auth.MemberInfoResponse;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/info")
    public ResponseEntity<?> memberInfo(@RequestHeader("Authorization") String authorizationHeader){
        String accessToken = jwtUtil.getBearer(authorizationHeader);
        String email = jwtUtil.getEmail(accessToken);
        log.info("찾은 이메일: {}",email);
        MemberInfoResponse memberInfo = memberService.getMemberInfo(email);
        return ResponseEntity.ok().body(memberInfo);
    }
}
