package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.config.jwt.RefreshToken;
import cotato.csquiz.config.jwt.RefreshTokenRepository;
import cotato.csquiz.config.jwt.Token;
import cotato.csquiz.domain.dto.auth.JoinRequest;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.dto.ReissueResponse;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void createLoginInfo(JoinRequest request) {

        validateService.checkDuplicateEmail(request.getEmail());
        validateService.checkDuplicatePhoneNumber(request.getPhoneNumber());

        log.info("[회원 가입 서비스] : {}, {}, {}", request.getEmail(), request.getPassword(), request.getPassword());

        Member newMember = Member.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        memberRepository.save(newMember);
    }

    @Transactional
    public ReissueResponse reissue(String refreshToken) {

        if (jwtUtil.isExpired(refreshToken)) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        RefreshToken findToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (findToken.getRefreshToken().equals(refreshToken)) {
            Token token = jwtUtil.createToken(email, role);
            findToken.updateRefreshToken(token.getRefreshToken());
            log.info("재발급 된 액세스 토큰: {}", token.getAccessToken());
            return ReissueResponse.builder()
                    .accessToken(token.getAccessToken())
                    .build();
        }
        return null;
    }
}
