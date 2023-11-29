package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.JoinRequest;
import cotato.csquiz.dto.ReissueResponse;
import cotato.csquiz.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<?> joinAuth(@RequestBody JoinRequest request) {
        authService.createLoginInfo(request);
        log.info("[회원 가입 컨트롤러] : {}, {}, {}", request.getEmail(), request.getPassword(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> tokenReissue(@CookieValue(name = "refreshToken")String refreshToken){
        ReissueResponse reissue = authService.reissue(refreshToken);
        return ResponseEntity.ok().body(reissue);
    }

}
