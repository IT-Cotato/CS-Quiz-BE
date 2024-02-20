package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    WEBSOCKET_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "소캣 메세지 전송 실패"),
    GENERATION_NUMBER_EXIST(HttpStatus.CONFLICT, "같은 숫자의 기수가 있습니다"),
    EDUCATION_EXIST(HttpStatus.CONFLICT, "이미 교육이 존재합니다"),
    IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "변경할 이미지가 없습니다"),
    DATA_NOTFOUND(HttpStatus.NOT_FOUND, "데이터가 없습니다"),
    DATE_INVALID(HttpStatus.BAD_REQUEST, "시작날짜가 끝 날짜보다 뒤입니다"),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "존재하는 이메일 입니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "존재하는 전화번호입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인 요청에 실패했습니다."),
    JWT_NOT_EXISTS(HttpStatus.UNAUTHORIZED, "Jwt 토큰이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    ROLE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 ROLE은 변경할 수 없습니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "요청하신 코드가 일치하지 않습니다."),
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "구글, 네이버 형식으로 입력해주세요"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    CREATE_VERIFY_CODE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "코드 생성에 실패했습니다."),
    SUBJECT_INVALID(HttpStatus.BAD_REQUEST, "교육 주제는 NULL이거나 비어있을 수 없습니다."),
    NAME_NOT_MATCH(HttpStatus.NOT_FOUND, "해당 유저의 전화번호와 이름이 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "이전과 다른 비밀번호로 변경해주세요."),
    ROLE_IS_NOT_OLD_MEMBER(HttpStatus.BAD_REQUEST, "해당 회원의 ROLE은 OLD_MEMBER가 아닙니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "포지션을 선택해주세요."),
    GENERATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 Generation을 찾을 수 없습니다."),
    EDUCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 교육을 찾을 수 없습니다."),
    QUIZ_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "퀴즈 번호는 중복될 수 없습니다."),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 퀴즈를 찾을 수 없습니다."),
    QUIZ_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "해당 문제는 접근이 불가능한 상태입니다. 접근을 허용해주십시오"),
    MEMBER_CANT_ACCESS(HttpStatus.BAD_REQUEST, "해당 멤버의 ROLE로 접근할 수 없습니다"),
    RANDOM_NUMBER_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "랜덤 숫자 생성 실패"),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "accessToken이 만료되었습니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션이 존재하지 않습니다."),
    ANSWER_VALIDATION_FAULT(HttpStatus.BAD_REQUEST, "사용될 수 없는 정답"),
    ALREADY_REPLY_CORRECT(HttpStatus.BAD_REQUEST, "이미 해당 문제에 정답 처리되셨습니다."),
    REGRADE_FAIL(HttpStatus.BAD_REQUEST, "재채점 할 Record가 없습니다."),
    QUIZ_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "주관식 정답만 추가 가능합니다."),
    CONTENT_IS_NOT_ANSWER(HttpStatus.BAD_REQUEST, "해당 문구는 정답이 아닙니다"),
    CONTENT_IS_ALREADY_ANSWER(HttpStatus.BAD_REQUEST, "이미 정답인 답을 추가했습니다"),
    ENUM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 Enum을 resolve할 수 없습니다."),
    EDUCATION_CLOSED(HttpStatus.BAD_REQUEST, "CS 퀴즈가 닫혀 있습니다 먼저 교육 시작 버튼을 눌러주세요"),
    SCORER_NOT_FOUND(HttpStatus.NOT_FOUND, "10번문제 득점자가 아직 존재하지 않습니다"),
    KING_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "아직 킹 멤버 풀사람을 계산 중입니다");

    private final HttpStatus httpStatus;
    private final String message;
}