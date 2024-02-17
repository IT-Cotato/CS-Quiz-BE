package cotato.csquiz.config.jwt;

import cotato.csquiz.exception.FilterAuthenticationException;
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
            throw new FilterAuthenticationException("토큰 형태 오류");
        }
        return getBearer(header);
    }

    public String getBearer(String authorizationHeader) {
        return authorizationHeader.replace("Bearer", "");
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }

    public Token createToken(String email, String authority) {
        return Token.builder()
                .accessToken(createAccessToken(email, authority))
                .refreshToken(createRefreshToken(email, authority))
                .build();
    }

    @Transactional
    public void setBlackList(String token) {
        String id = getEmail(token);
        RefreshToken findToken = refreshTokenRepository.findById(id)
                .orElseThrow();
        refreshTokenRepository.delete(findToken);
        BlackList blackList = BlackList.builder()
                .id(findToken.getRefreshToken())
                .ttl(getExpiration(findToken.getRefreshToken()))
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
}
