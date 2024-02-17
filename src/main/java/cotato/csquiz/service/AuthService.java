package cotato.csquiz.service;

import cotato.csquiz.config.jwt.BlackList;
import cotato.csquiz.config.jwt.BlackListRepository;
import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.config.jwt.RefreshToken;
import cotato.csquiz.config.jwt.RefreshTokenRepository;
import cotato.csquiz.config.jwt.Token;
import cotato.csquiz.domain.constant.EmailConstants;
import cotato.csquiz.domain.dto.auth.FindPasswordResponse;
import cotato.csquiz.domain.dto.auth.JoinRequest;
import cotato.csquiz.domain.dto.auth.LogoutRequest;
import cotato.csquiz.domain.dto.auth.ReissueResponse;
import cotato.csquiz.domain.dto.email.SendEmailRequest;
import cotato.csquiz.domain.dto.member.MemberEmailResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String EMAIL_DELIMITER = "@";
    private static final int EXPOSED_LENGTH = 4;
    private static final String REFRESH_TOKEN = "refreshToken";

    private final MemberRepository memberRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final EmailVerificationService emailVerificationService;

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenAge;

    @Transactional
    public void createLoginInfo(JoinRequest request) {

        validateService.checkDuplicateEmail(request.getEmail());
        validateService.checkDuplicatePhoneNumber(request.getPhoneNumber());

        log.info("[회원 가입 서비스]: {}, {}", request.getEmail(), request.getPassword());
        Member newMember = Member.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        memberRepository.save(newMember);
    }

    @Transactional
    public ReissueResponse reissue(String refreshToken, HttpServletResponse response) {
        if (jwtUtil.isExpired(refreshToken) || blackListRepository.existsById(refreshToken)) {
            log.warn("블랙리스트에 존재하는 토큰: {}", blackListRepository.existsById(refreshToken));
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        RefreshToken findToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        if (!refreshToken.equals(findToken.getRefreshToken())) {
            throw new AppException(ErrorCode.JWT_NOT_EXISTS);
        }
        jwtUtil.setBlackList(refreshToken);
        Token token = jwtUtil.createToken(email, role);
        findToken.updateRefreshToken(token.getRefreshToken());
        refreshTokenRepository.save(findToken);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, token.getRefreshToken());
        refreshCookie.setMaxAge(refreshTokenAge / 1000);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
        return ReissueResponse.from(token.getAccessToken());
    }

    @Transactional
    public void logout(LogoutRequest request, String refreshToken) {
        String email = jwtUtil.getEmail(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.JWT_NOT_EXISTS));
        log.info("로그아웃된 토큰 블랙리스트 처리");
        jwtUtil.setBlackList(existRefreshToken.getRefreshToken());
        BlackList blackList = BlackList.builder()
                .id(request.accessToken())
                .ttl(jwtUtil.getExpiration(existRefreshToken.getRefreshToken()))
                .build();
        blackListRepository.save(blackList);
        refreshTokenRepository.delete(existRefreshToken);
    }

    public void sendSignUpEmail(SendEmailRequest request) {
        validateService.emailNotExist(request.getEmail());
        emailVerificationService.sendVerificationCodeToEmail(request.getEmail(), EmailConstants.SIGNUP_SUBJECT);
    }

    public void verifySingUpCode(String email, String code) {
        emailVerificationService.verifyCode(email, code);
    }

    public void sendFindPasswordEmail(SendEmailRequest request) {
        validateService.emailExist(request.getEmail());
        emailVerificationService.sendVerificationCodeToEmail(request.getEmail(), EmailConstants.PASSWORD_SUBJECT);
    }

    public FindPasswordResponse verifyPasswordCode(String email, String code) {
        emailVerificationService.verifyCode(email, code);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        String role = findMember.getRole().getKey();
        Token token = jwtUtil.createToken(email, role);
        return FindPasswordResponse.builder()
                .accessToken(token.getAccessToken())
                .build();
    }

    public MemberEmailResponse findMemberEmail(String name, String phoneNumber) {
        Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        validateMatchName(findMember.getName(), name);
        String maskedId = getMaskId(findMember.getEmail());
        return MemberEmailResponse.from(maskedId);
    }

    private String getMaskId(String email) {
        String originId = email.split(EMAIL_DELIMITER)[0];
        String maskedPart = "*".repeat(EXPOSED_LENGTH);
        return originId.substring(0, 4) + maskedPart + EMAIL_DELIMITER + email.split(EMAIL_DELIMITER)[1];
    }

    private void validateMatchName(String originName, String requestName) {
        if (!originName.equals(requestName)) {
            throw new AppException(ErrorCode.NAME_NOT_MATCH);
        }
    }

    public boolean isBlocked(String token) {
        return blackListRepository.existsById(token);
    }
}
