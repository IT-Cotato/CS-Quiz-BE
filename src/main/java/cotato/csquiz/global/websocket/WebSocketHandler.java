package cotato.csquiz.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cotato.csquiz.domain.dto.socket.CsQuizStopResponse;
import cotato.csquiz.domain.dto.socket.QuizStartResponse;
import cotato.csquiz.domain.dto.socket.QuizStatusResponse;
import cotato.csquiz.domain.dto.socket.QuizStopResponse;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.MemberRole;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.service.QuizService;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private static final String KING_COMMAND = "king";
    private static final String WINNER_COMMAND = "winner";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizService quizService;

    @Autowired
    public WebSocketHandler(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberEmail = findAttributeByToken(session, "email");
        String educationId = findAttributeByToken(session, "educationId");
        log.info(memberEmail);
        boolean isGeneral = connectSession(session, memberEmail); //true : 일반 회원 false : 관리자
        if (isGeneral) {
            checkQuizAlreadyStart(session, Long.parseLong(educationId));
        }
        log.info("CLIENTS: {}", CLIENTS.keySet());
        log.info("MANAGERS: {}", MANAGERS.keySet());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberEmail = findAttributeByToken(session, "email");
        if (memberEmail != null) {
            String roleString = findAttributeByToken(session, "role");
            log.info("[연결된 역할] {}", roleString);
            MemberRole role = MemberRole.valueOf(roleString.split("_")[1]);
            disconnectSession(memberEmail, role);
        }
        log.info("[disconnect the session]");
        log.info("CLIENTS: {}", CLIENTS.keySet());
        log.info("MANAGERS: {}", MANAGERS.keySet());
    }

    public void accessQuiz(long quizId) {
        try {
            QuizStatusResponse response = QuizStatusResponse.builder()
                    .quizId(quizId)
                    .command("show")
                    .status(QuizStatus.QUIZ_ON)
                    .start(QuizStatus.QUIZ_OFF)
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
                    .quizId(quizId)
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

    public void stopQuiz(Quiz quiz) {
        String command = "";
        if (quiz.getNumber() == 9) {
            command = KING_COMMAND;
        }
        if (quiz.getNumber() == 10) {
            command = WINNER_COMMAND;
        }
        try {
            QuizStopResponse response = QuizStopResponse.from(command, quiz.getId());
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            for (WebSocketSession clientSession : CLIENTS.values()) {
                clientSession.sendMessage(responseMessage);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    public void stopAllQuiz(Long educationId) {
        try {
            CsQuizStopResponse response = CsQuizStopResponse.from("exit", educationId);
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            for (WebSocketSession clientSession : CLIENTS.values()) {
                clientSession.sendMessage(responseMessage);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    private boolean connectSession(WebSocketSession session, String memberEmail) {
        if (memberEmail != null) {
            String roleString = findAttributeByToken(session, "role");
            log.info("roleString {}", roleString);
            MemberRole role = MemberRole.valueOf(roleString.split("_")[1]);
            log.info("role role {}", role);
            return updateSession(session, memberEmail, role);
        } else {
            log.warn("Email is null in session {}.", session.getId());
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    private boolean updateSession(WebSocketSession session, String memberEmail, MemberRole role) {
        return switch (role) {
            case EDUCATION -> {
                updateManagerSession(memberEmail, session);
                yield false;
            }
            case MEMBER, ADMIN -> {
                updateClientSession(memberEmail, session);
                yield true;
            }
            default -> {
                log.warn("Unknown role for member {}.", memberEmail);
                throw new AppException(ErrorCode.MEMBER_CANT_ACCESS);
            }
        };
    }

    private void updateManagerSession(String memberEmail, WebSocketSession session) {
        MANAGERS.put(memberEmail, session);
        log.info("{} connect with Session {} in MANAGER", memberEmail, session);
    }

    private void updateClientSession(String memberEmail, WebSocketSession session) {
        CLIENTS.put(memberEmail, session);
        log.info("{} connect with Session {} in CLIENTS", memberEmail, session);
    }

    private String findAttributeByToken(WebSocketSession session, String key) {
        return (String) session.getAttributes().get(key);
    }

    private void checkQuizAlreadyStart(WebSocketSession session, Long educationId) {
        log.info("checkQuizAlreadyStart Start");
        try {
            QuizStatusResponse response = quizService.checkQuizStarted(educationId);
            String json = objectMapper.writeValueAsString(response);
            TextMessage responseMessage = new TextMessage(json);
            session.sendMessage(responseMessage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }

    private void disconnectSession(String memberEmail, MemberRole role) {
        switch (role) {
            case EDUCATION -> {
                MANAGERS.remove(memberEmail);
            }
            case MEMBER, ADMIN -> {
                CLIENTS.remove(memberEmail);
            }
            default -> {
                throw new AppException(ErrorCode.MEMBER_CANT_ACCESS);
            }
        }
    }
}
