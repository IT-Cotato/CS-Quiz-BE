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
import cotato.csquiz.repository.QuizRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private static final String KING_COMMAND = "king";
    private static final String WINNER_COMMAND = "winner";
    private static final String SHOW_COMMAND = "show";
    private static final String START_COMMAND = "start";
    private static final String EXIT_COMMAND = "exit";
    private static final String MEMBER_ID_KEY = "memberId";
    private static final String EDUCATION_ID_KEY = "educationId";
    private static final String ROLE_KEY = "role";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizRepository quizRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
        String educationId = findAttributeByToken(session, EDUCATION_ID_KEY);
        String role = findAttributeByToken(session, ROLE_KEY);
        MemberRole memberRole = findMemberRole(role);

        addMemberToSession(memberId, memberRole, session);

        if (isQuizAlreadyStarted(Long.parseLong(educationId)) && isMemberClient(memberRole)) {
            sendStartedQuiz(Long.parseLong(educationId), session);
        }

        log.info("[세션 연결] {}", memberId);
    }

    private void sendStartedQuiz(Long educationId, WebSocketSession session) {
        Optional<Quiz> maybeQuiz = quizRepository.findByStatusAndEducationId(QuizStatus.QUIZ_ON,
                educationId);
        QuizStatusResponse response = maybeQuiz.map(quiz -> QuizStatusResponse.builder()
                        .command(SHOW_COMMAND)
                        .quizId(quiz.getId())
                        .status(quiz.getStatus())
                        .start(quiz.getStart())
                        .build())
                .orElse(QuizStatusResponse.builder()
                        .command(SHOW_COMMAND)
                        .build());
        sendMessage(session, response);
    }

    private boolean isQuizAlreadyStarted(Long educationId) {
        return quizRepository.findByStatusAndEducationId(QuizStatus.QUIZ_ON, educationId).isPresent();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
        String role = findAttributeByToken(session, ROLE_KEY);
        MemberRole memberRole = findMemberRole(role);

        if (isMemberClient(memberRole)) {
            CLIENTS.remove(memberId);
        } else {
            MANAGERS.remove(memberId);
        }

        log.info("[세션 종료] {}", memberId);
    }

    public void accessQuiz(Long quizId) {
        QuizStatusResponse response = QuizStatusResponse.builder()
                .quizId(quizId)
                .command(SHOW_COMMAND)
                .status(QuizStatus.QUIZ_ON)
                .start(QuizStatus.QUIZ_OFF)
                .build();

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void startQuiz(Long quizId) {
        QuizStartResponse response = QuizStartResponse.builder()
                .quizId(quizId)
                .command(START_COMMAND)
                .build();

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
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
        QuizStopResponse response = QuizStopResponse.from(command, quiz.getId());
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void stopAllQuiz(Long educationId) {
        CsQuizStopResponse response = CsQuizStopResponse.from(EXIT_COMMAND, educationId);
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    private MemberRole findMemberRole(String roleName) {
        return MemberRole.fromKey(roleName);
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

    private void addMemberToSession(String memberId, MemberRole role, WebSocketSession session) {
        if (isMemberClient(role)) {
            CLIENTS.put(memberId, session);
            log.info("{} connect with Session {} in CLIENTS", memberId, session);
        } else {
            MANAGERS.put(memberId, session);
            log.info("{} connect with Session {} in MANAGER", memberId, session);
        }
    }

    private String findAttributeByToken(WebSocketSession session, String key) {
        return session.getAttributes().get(key).toString();
    }

    private void sendMessage(WebSocketSession session, Object sendValue) {
        try {
            String json = objectMapper.writeValueAsString(sendValue);
            TextMessage responseMessage = new TextMessage(json);
            session.sendMessage(responseMessage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }
}
