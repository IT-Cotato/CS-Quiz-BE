package cotato.csquiz.config.jwt;

import cotato.csquiz.domain.constant.TokenConstants;
import cotato.csquiz.exception.FilterAuthenticationException;
import cotato.csquiz.exception.InterceptorException;
import cotato.csquiz.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secretKey}")
    String secretKey;

    @Value("${jwt.access.expiration}")
    Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    Long refreshExpiration;

    private static final Long SOCKET_TOKEN_EXPIRATION = 1000 * 30L;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final MemberRepository memberRepository;

    public boolean isExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("email", String.class);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new FilterAuthenticationException("Bearer 토큰이 존재하지 않습니다.");
        }
        return getBearer(header);
    }

    public String resolveWithAccessToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new FilterAuthenticationException("토큰 형태 오류");
        }
        return getBearer(token);
    }

    public String getBearer(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }

    public String getType(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("type", String.class);
    }

    public Token createToken(String email, String authority) {
        return Token.builder()
                .accessToken(createAccessToken(email, authority))
                .refreshToken(createRefreshToken(email, authority))
                .build();
    }

    @Transactional
    public void setBlackList(String token) {
        BlackList blackList = BlackList.builder()
                .id(token)
                .ttl(getExpiration(token))
                .build();
        blackListRepository.save(blackList);
    }

    public Long getExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - new Date().getTime();
    }

    private String createAccessToken(String email, String authority) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", authority);
        claims.put("type", TokenConstants.ACCESS_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(String email, String authority) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", authority);
        claims.put("type", TokenConstants.REFRESH_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validateMemberExist(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new FilterAuthenticationException("존재하지 않는 회원입니다.");
        }
    }

    public String createSocketToken(String email, String role) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("type", TokenConstants.SOCKET_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SOCKET_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validateSocketToken(String socketToken) {
        String tokenType = getType(socketToken);
        if (!TokenConstants.SOCKET_TOKEN.equals(tokenType)) {
            throw new InterceptorException("소켓 토큰을 이용해주세요.");
        }
    }

    public void validateAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new FilterAuthenticationException("액세스 토큰을 넣고 요청해주세요.");
        }
        if (!TokenConstants.ACCESS_TOKEN.equals(getType(accessToken))) {
            throw new FilterAuthenticationException("액세스 토큰을 사용해주세요.");
        }
        if (isExpired(accessToken)) {
            throw new FilterAuthenticationException("토큰이 만료되었습니다.");
        }
        if (isBlocked(accessToken)) {
            throw new FilterAuthenticationException("차단된 토큰입니다.");
        }
    }

    private boolean isBlocked(final String token) {
        return blackListRepository.existsById(token);
    }
}
