package cotato.csquiz.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import cotato.csquiz.controller.dto.socket.CsQuizStopResponse;
import cotato.csquiz.controller.dto.socket.QuizStartResponse;
import cotato.csquiz.controller.dto.socket.QuizStatusResponse;
import cotato.csquiz.controller.dto.socket.QuizStopResponse;
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
        String memberId = findAttributeByToken(session, "memberId");
        String educationId = findAttributeByToken(session, "educationId");

        MemberRole memberRole = findMemberRole(session, memberId);
        Boolean isMemberClient = isMemberClient(memberRole);

        if (isMemberClient) {
            addClientSession(memberId,session);
            checkQuizAlreadyStart(session, Long.parseLong(educationId));
        } else {
            addManagerSession(memberId,session);
        }

        log.info("[세션 연결] {}", memberId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberId = findAttributeByToken(session, "memberId");

        MemberRole role = findMemberRole(session, memberId);
        Boolean isMemberClient = isMemberClient(role);

        if (isMemberClient) {
            CLIENTS.remove(memberId);
        } else {
            MANAGERS.remove(memberId);
        }

        log.info("[세션 종료] {}", memberId);
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

    private MemberRole findMemberRole(WebSocketSession session, String memberId) {
        if (memberId != null) {
            String roleString = findAttributeByToken(session, "role");
            MemberRole role = MemberRole.valueOf(roleString.split("_")[1]);
            log.info("[소켓 MemberRole] {}", role);
            return role;
        } else {
            log.warn("해당 WebSocketSession에 멤버가 없습니다 {}.", session.getId());
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    private Boolean isMemberClient(MemberRole memberRole) {
        return switch (memberRole) {
            case EDUCATION -> false;
            case MEMBER, ADMIN -> true;
            default -> {
                log.warn("Unknown role for member {}.", memberRole);
                throw new AppException(ErrorCode.MEMBER_CANT_ACCESS);
            }
        };
    }

    private void addManagerSession(String memberId, WebSocketSession session) {
        MANAGERS.put(memberId, session);
        log.info("{} connect with Session {} in MANAGER", memberId, session);
    }

    private void addClientSession(String memberId, WebSocketSession session) {
        CLIENTS.put(memberId, session);
        log.info("{} connect with Session {} in CLIENTS", memberId, session);
    }

    private String findAttributeByToken(WebSocketSession session, String key) {
        return session.getAttributes().get(key).toString();
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
}
