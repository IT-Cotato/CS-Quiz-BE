package cotato.csquiz.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cotato.csquiz.domain.dto.socket.QuizStatusResponse;
import cotato.csquiz.domain.dto.socket.QuizStartResponse;
import cotato.csquiz.domain.entity.MemberRole;
import cotato.csquiz.domain.entity.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizService quizService;

    @Autowired
    public WebSocketHandler(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberEmail = findAttributeByToken(session, "email");
        log.info(memberEmail);
        boolean isGeneral = connectSession(session, memberEmail); //true : 일반 회원 false : 관리자
        if (isGeneral) {
            checkQuizAlreadyStart(session);
        }
        log.info("CLIENTS: {} MANAGERS: {}", CLIENTS, MANAGERS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberEmail = findAttributeByToken(session, "email");
        CLIENTS.remove(memberEmail);
        log.info("disconnect the session");
        log.info(CLIENTS.toString());
    }

    public void accessQuiz(long quizId) {
        try {
            QuizStatusResponse response = QuizStatusResponse.builder()
                    .quizNum(quizId)
                    .command("show")
                    .status(QuizStatus.ON)
                    .build();
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            for (WebSocketSession clientSession : CLIENTS.values()) {
                clientSession.sendMessage(responseMessage);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    public void startQuiz(Long quizId) {
        try {
            QuizStartResponse response = QuizStartResponse.builder()
                    .quizNum(quizId)
                    .command("start")
                    .build();
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            for (WebSocketSession clientSession : CLIENTS.values()) {
                clientSession.sendMessage(responseMessage);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    private static boolean connectSession(WebSocketSession session, String memberEmail) {
        if (memberEmail != null) {
            String roleString = findAttributeByToken(session, "role");
            log.info("roleString {}",roleString);
            MemberRole role = MemberRole.valueOf(roleString.split("_")[1]);
            log.info("role role {}",role);
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
            case MEMBER -> {
                updateClientSession(memberEmail, session);
                yield true;
            }
            default -> {
                log.warn("Unknown role for member {}.", memberEmail);
                throw new AppException(ErrorCode.MEMBER_CANT_ACCESS);
            }
        };
    }

    private static void updateManagerSession(String memberEmail, WebSocketSession session) {
        MANAGERS.put(memberEmail, session);
        log.info("{} connect with Session {} in MANAGER", memberEmail, session);
    }

    private static void updateClientSession(String memberEmail, WebSocketSession session) {
        CLIENTS.put(memberEmail, session);
        log.info("{} connect with Session {} in CLIENTS", memberEmail, session);
    }

    private static String findAttributeByToken(WebSocketSession session, String key) {
        return (String) session.getAttributes().get(key);
    }

    private void checkQuizAlreadyStart(WebSocketSession session) {
        log.info("checkQuizAlreadyStart Start");
        try {
            QuizStatusResponse response = quizService.checkQuizStarted();
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            session.sendMessage(responseMessage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }
}