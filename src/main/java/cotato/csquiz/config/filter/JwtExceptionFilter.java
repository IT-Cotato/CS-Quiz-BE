package cotato.csquiz.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ErrorResponse;
import cotato.csquiz.exception.FilterAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.JWT_EXPIRED.getMessage(),
                    request.getRequestURI()
            );
            setErrorResponse(response, errorResponse);
        } catch (MalformedJwtException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST,
                    "jwt 토큰 형식으로 입력해주세요.",
                    request.getRequestURI()
            );
            setErrorResponse(response, errorResponse);
        } catch (FilterAuthenticationException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED,
                    e.getMessage(),
                    request.getRequestURI()
            );
            setErrorResponse(response, errorResponse);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorResponse errorResponse)
            throws IOException {
        log.error("jwt토큰 인증 에러 발생, 타입 {}", errorResponse.message());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        response.setStatus(errorResponse.status().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
