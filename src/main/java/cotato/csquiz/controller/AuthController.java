package cotato.csquiz.controller;

import cotato.csquiz.domain.dto.auth.JoinRequest;
import cotato.csquiz.domain.dto.auth.ReissueResponse;
import cotato.csquiz.domain.dto.email.SendEmailRequest;
import cotato.csquiz.domain.dto.member.MemberEmailResponse;
import cotato.csquiz.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        log.info("[회원 가입 컨트롤러] : {}, {}, {}", request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> tokenReissue(@CookieValue(name = "refreshToken") String refreshToken) {
        ReissueResponse reissue = authService.reissue(refreshToken);
        return ResponseEntity.ok().body(reissue);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<?> sendSignUpVerificationCode(@Valid @RequestBody SendEmailRequest request) {
        authService.sendSignUpEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<?> verifyCode(@RequestParam(name = "email") String email,
                                        @RequestParam(name = "code") String code) {
        authService.verifySingUpCode(email, code);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/verification", params = "type=find-password")
    public ResponseEntity<?> sendFindPasswordVerificationCode(@RequestBody SendEmailRequest request) {
        authService.sendFindPasswordEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/verification", params = "type=find-password")
    public ResponseEntity<?> verifyPasswordCode(@RequestParam(name = "email") String email,
                                                @RequestParam(name = "code") String code) {
        return ResponseEntity.ok().body(authService.verifyPasswordCode(email, code));
    }

    @GetMapping("/email")
    public ResponseEntity<?> findEmail(@RequestParam(name = "name") String name,
                                       @RequestParam("phone") String phoneNumber) {
        MemberEmailResponse memberEmail = authService.findMemberEmail(name, phoneNumber);
        log.info("아이디 찾기 컨트롤러: {}", name);
        return ResponseEntity.ok(memberEmail);
    }
}
