package cotato.csquiz.global.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        List<String> memberList = request.getHeaders().get("member"); //JWT로 email을 찾을 수 있으면 바꾸기 TODO
        attributes.put("member", memberList != null ? memberList.get(0) : null);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
