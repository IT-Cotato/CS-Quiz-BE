package cotato.csquiz.global.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.service.GenerationService;
import cotato.csquiz.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GenerationService generationService; //추후 변경

    @Autowired
    public WebSocketHandler(GenerationService generationService) {
        this.generationService = generationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberEmail = (String) session.getAttributes().get("member");
        log.info(memberEmail);
        connectSession(session, memberEmail);
        //현재 진행중인 문제가 있는지 확인

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberEmail = (String) session.getAttributes().get("member");
        CLIENTS.remove(memberEmail);
        log.info("disconnect the session");
        log.info(CLIENTS.toString());
    }

    private static void connectSession(WebSocketSession session, String memberEmail) {
        if (memberEmail != null) {
            // TODO: findROLE logic
            MemberRole role = findRoleForMember(memberEmail); // TODO: 실제로 사용할 MemberRole을 찾는 로직으로 대체
            updateSession(session, memberEmail, role);
            log.info("CLIENTS: {} MANAGERS: {}", CLIENTS, MANAGERS);
        } else {
            log.warn("Email is null in session {}.", session.getId());
        }
    }

    private static void updateSession(WebSocketSession session, String memberEmail, MemberRole role) {
        switch (role) {
            case EDUCATION, ADMIN:
                updateManagerSession(memberEmail, session);
                break;
            case GENERAL:
                updateClientSession(memberEmail, session);
                break;
            default:
                log.warn("Unknown role for member {}.", memberEmail);
        }
    }

    private static MemberRole findRoleForMember(String memberEmail) {
        // TODO: 실제로 사용할 MemberRole을 찾는 로직을 구현
        if (memberEmail.equals("gikhoon@naver.com")) {
            return MemberRole.GENERAL;
        }
        return MemberRole.EDUCATION; // TODO: 실제로 사용할 MemberRole을 찾는 로직을 구현
    }

    private static void updateManagerSession(String memberEmail, WebSocketSession session) {
        MANAGERS.remove(memberEmail);
        MANAGERS.put(memberEmail, session);
        log.info("{} connect with Session {} in MANAGER", memberEmail, session);
    }

    private static void updateClientSession(String memberEmail, WebSocketSession session) {
        CLIENTS.remove(memberEmail);
        CLIENTS.put(memberEmail, session);
        log.info("{} connect with Session {} in CLIENTS", memberEmail, session);
    }
}