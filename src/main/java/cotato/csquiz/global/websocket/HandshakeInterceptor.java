package cotato.csquiz.global.websocket;

import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.domain.enums.TokenType;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String socketToken = request.getURI().getQuery().split("=")[1];
        jwtUtil.validateSocketToken(socketToken);
        String role = jwtUtil.getRole(socketToken);
        String email = jwtUtil.getEmail(socketToken);
        attributes.put("email", email);
        attributes.put("role", role);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
