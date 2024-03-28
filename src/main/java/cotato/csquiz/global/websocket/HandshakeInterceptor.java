package cotato.csquiz.global.websocket;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.enums.MemberRole;
import cotato.csquiz.exception.InterceptorException;
import cotato.csquiz.exception.InterceptorRoleException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@RequiredArgsConstructor
@Slf4j
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake");
        String[] querySplit = request.getURI().getQuery().split("&");
        String socketToken = null;
        String educationId = null;
        for (String query : querySplit) {
            String[] keyValue = query.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                if (key.equals("Authorization")) {
                    socketToken = value;
                } else if (key.equals("educationId")) {
                    educationId = value;
                }
            }
        }

        try {
            jwtUtil.validateSocketToken(socketToken);
            String role = jwtUtil.getRole(socketToken);
            String email = jwtUtil.getEmail(socketToken);
            attributes.put("email", email);
            attributes.put("role", role);
            attributes.put("educationId", educationId);
            validateRole(role);
        } catch (InterceptorException | ExpiredJwtException | MalformedJwtException | SignatureException exception) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } catch (InterceptorRoleException e) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    private void validateRole(String role) {
        MemberRole memberRole = MemberRole.fromKey(role);
        if (MemberRole.EDUCATION != memberRole && MemberRole.ADMIN != memberRole && MemberRole.MEMBER != memberRole) {
            throw new InterceptorRoleException("해당 역할은 WS 연결이 불가능합니다.");
        }
    }
}
