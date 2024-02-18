package cotato.csquiz.global.websocket;

import cotato.csquiz.config.jwt.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        String socketToken = request.getURI().getQuery().split("=")[1];
        String role = jwtUtil.getRole(socketToken);
        String email = jwtUtil.getEmail(socketToken);
        attributes.put("email", email);
        attributes.put("role", role);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
