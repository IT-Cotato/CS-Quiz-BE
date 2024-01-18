package cotato.csquiz.global.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cotato.csquiz.domain.dto.quiz.QuizStatusResponse;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.service.GenerationService;
import cotato.csquiz.service.QuizService;
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

    private final QuizService quizService;

    @Autowired
    public WebSocketHandler(GenerationService generationService, QuizService quizService) {
        this.generationService = generationService;
        this.quizService = quizService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberEmail = (String) session.getAttributes().get("member");
        log.info(memberEmail);
        boolean isGeneral = connectSession(session, memberEmail); //true : 일반 회원 false : 관리자
        if (isGeneral) {
            checkQuizAlreadyStart(session);
        }
        log.info("CLIENTS: {} MANAGERS: {}", CLIENTS, MANAGERS);
    }

    private void checkQuizAlreadyStart(WebSocketSession session) {
        log.info("checkQuizAlreadyStart Start");
        try {
            QuizStatusResponse response = quizService.checkQuizStarted();
            if (response != null) {
                String json = objectMapper.writeValueAsString(response);
                TextMessage responseMessage = new TextMessage(json);
                session.sendMessage(responseMessage);
            } else {
                log.info("there is no Started Quiz");
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberEmail = (String) session.getAttributes().get("member");
        CLIENTS.remove(memberEmail);
        log.info("disconnect the session");
        log.info(CLIENTS.toString());
    }

    private static boolean connectSession(WebSocketSession session, String memberEmail) {
        if (memberEmail != null) {
            // TODO: findROLE logic
            MemberRole role = findRoleForMember(memberEmail); // TODO: 실제로 사용할 MemberRole 찾는 로직으로 대체
            return updateSession(session, memberEmail, role);
        } else {
            log.warn("Email is null in session {}.", session.getId());
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    private static boolean updateSession(WebSocketSession session, String memberEmail, MemberRole role) {
        return switch (role) {
            case EDUCATION, ADMIN -> {
                updateManagerSession(memberEmail, session);
                yield false;
            }
            case GENERAL -> {
                updateClientSession(memberEmail, session);
                yield true;
            }
            default -> {
                log.warn("Unknown role for member {}.", memberEmail);
                throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
            }
        };
    }

    private static MemberRole findRoleForMember(String memberEmail) {
        // TODO: 실제로 사용할 MemberRole 찾는 로직을 구현
        if (memberEmail.equals("gikhoon@naver.com")) {
            return MemberRole.GENERAL;
        }
        return MemberRole.EDUCATION; // TODO: 실제로 사용할 MemberRole 찾는 로직을 구현
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