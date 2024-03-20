package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token Already Expired"),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "RefreshToken is not in Repository."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인 요청에 실패했습니다."),
    REISSUE_FAIL(HttpStatus.UNAUTHORIZED, "재발급 요청 실패"),
    FILTER_EXCEPTION(HttpStatus.UNAUTHORIZED, "필터 내부에러 발생"),
    JWT_FORM_ERROR(HttpStatus.UNAUTHORIZED, "jwt 형식 에러 발생"),

    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 entity를 찾을 수 없습니다."),

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "존재하는 이메일 입니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "이전과 같은 비밀번호로 변경할 수 없습니다."),
    GENERATION_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "같은 숫자의 기수가 있습니다"),
    EDUCATION_DUPLICATED(HttpStatus.CONFLICT, "이미 교육이 존재합니다"),
    QUIZ_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "퀴즈 번호는 중복될 수 없습니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "존재하는 전화번호입니다."),

    ENUM_NOT_RESOLVED(HttpStatus.BAD_REQUEST, "입력한 Enum이 존재하지 않습니다."),
    DATE_INVALID(HttpStatus.BAD_REQUEST, "시작날짜가 끝 날짜보다 뒤입니다"),
    ROLE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 ROLE은 변경할 수 없습니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "요청하신 코드가 일치하지 않습니다."),
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "구글, 네이버 형식으로 입력해주세요"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    SUBJECT_INVALID(HttpStatus.BAD_REQUEST, "교육 주제는 NULL이거나 비어있을 수 없습니다."),
    ROLE_IS_NOT_OLD_MEMBER(HttpStatus.BAD_REQUEST, "해당 회원의 ROLE은 OLD_MEMBER가 아닙니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "포지션을 선택해주세요."),
    QUIZ_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "cannot access this quiz"),
    MEMBER_CANT_ACCESS(HttpStatus.BAD_REQUEST, "해당 멤버의 ROLE로 접근할 수 없습니다"),
    ANSWER_VALIDATION_FAULT(HttpStatus.BAD_REQUEST, "사용될 수 없는 정답"),
    ALREADY_REPLY_CORRECT(HttpStatus.BAD_REQUEST, "Already Correct"),
    REGRADE_FAIL(HttpStatus.BAD_REQUEST, "재채점 할 Record가 없습니다."),
    QUIZ_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "주관식 정답만 추가 가능합니다."),
    CONTENT_IS_NOT_ANSWER(HttpStatus.BAD_REQUEST, "해당 문구는 정답이 아닙니다"),
    CONTENT_IS_ALREADY_ANSWER(HttpStatus.BAD_REQUEST, "이미 정답인 답을 추가했습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "유효하지 않은 패스워드입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "Invalid Password"),
    EDUCATION_CLOSED(HttpStatus.BAD_REQUEST, "CS 퀴즈가 닫혀 있습니다 먼저 교육 시작 버튼을 눌러주세요"),

    WEBSOCKET_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "소캣 메세지 전송 실패"),
    IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다."),
    CREATE_VERIFY_CODE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "코드 생성에 실패했습니다."),
    RANDOM_NUMBER_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "랜덤 숫자 생성 실패"),
    INTERNAL_SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SQL 관련 에러 발생");

    private final HttpStatus httpStatus;
    private final String message;
}
