package cotato.csquiz.service;

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
import jakarta.persistence.EntityNotFoundException;
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
@Transactional(readOnly = true)
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
    private final EncryptService encryptService;

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenAge;

    @Transactional
    public void createLoginInfo(JoinRequest request) {
        validateService.checkDuplicateEmail(request.getEmail());
        validateService.checkPasswordPattern(request.getPassword());
        validateService.checkPhoneNumber(request.getPhoneNumber());

        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(request.getPhoneNumber());
        validateService.checkDuplicatePhoneNumber(encryptedPhoneNumber);
        log.info("[회원 가입 서비스]: {}, {}", request.getEmail(), request.getName());

        Member newMember = Member.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(encryptedPhoneNumber)
                .build();
        memberRepository.save(newMember);
    }

    @Transactional
    public ReissueResponse reissue(String refreshToken, HttpServletResponse response) {
        if (jwtUtil.isExpired(refreshToken) || blackListRepository.existsById(refreshToken)) {
            log.warn("블랙리스트에 존재하는 토큰: {}", blackListRepository.existsById(refreshToken));
            throw new AppException(ErrorCode.REISSUE_FAIL);
        }
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        RefreshToken findToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        log.info("[브라우저에서 들어온 쿠키] == [DB에 저장된 토큰], {}", refreshToken.equals(findToken.getRefreshToken()));
        if (!refreshToken.equals(findToken.getRefreshToken())) {
            log.warn("[쿠키로 들어온 토큰과 DB의 토큰이 일치하지 않음.]");
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }
        jwtUtil.setBlackList(refreshToken);
        Token token = jwtUtil.createToken(email, role);
        findToken.updateRefreshToken(token.getRefreshToken());
        refreshTokenRepository.save(findToken);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, token.getRefreshToken());
        refreshCookie.setMaxAge(refreshTokenAge / 1000);
        log.info("[리프레시 쿠키 발급, 발급시간 : {}]", refreshTokenAge / 1000);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
        return ReissueResponse.from(token.getAccessToken());
    }

    @Transactional
    public void logout(LogoutRequest request, String refreshToken, HttpServletResponse response) {
        String email = jwtUtil.getEmail(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));
        jwtUtil.setBlackList(refreshToken);
        log.info("[로그아웃 된 리프레시 토큰 블랙리스트 처리]");
        refreshTokenRepository.delete(existRefreshToken);
        Cookie deleteCookie = new Cookie(REFRESH_TOKEN, null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        deleteCookie.setSecure(true);
        deleteCookie.setHttpOnly(true);
        response.addCookie(deleteCookie);
        jwtUtil.setBlackList(request.accessToken());
        log.info("[로그아웃 된 액세스 토큰 블랙리스트 처리]");
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
        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        Member findMember = memberRepository.findByPhoneNumber(encryptedPhoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("해당 전화번호를 가진 회원을 찾을 수 없습니다."));
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
            throw new EntityNotFoundException("해당 이름을 가진 회원을 찾을 수 없습니다.");
        }
    }
}
