package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    GENERATION_NAME_EXIST(HttpStatus.CONFLICT, "이미 이름이 있습니다"),
    EDUCATION_EXIST(HttpStatus.CONFLICT, "이미 교육이 존재합니다"),
    IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "변경할 이미지가 없습니다"),
    DATA_NOTFOUND(HttpStatus.NOT_FOUND, "데이터가 없습니다"),
    DATE_INVALID(HttpStatus.BAD_REQUEST, "시작날짜가 끝 날짜보다 뒤입니다"),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "존재하는 이메일 입니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "존재하는 전화번호입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인 요청에 실패했습니다."),
    JWT_NOT_EXISTS(HttpStatus.NO_CONTENT, "Jwt 토큰이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
    ROLE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 ROLE은 변경할 수 없습니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "요청하신 코드가 일치하지 않습니다."),
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "구글, 네이버 형식으로 입력해주세요"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    CREATE_VERIFY_CODE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "코드 생성에 실패했습니다."),
    NAME_NOT_MATCH(HttpStatus.NOT_FOUND, "해당 유저의 전화번호와 이름이 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "이전과 다른 비밀번호로 변경해주세요."),
    EDUCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 교육을 찾을 수 없습니다."),
    QUIZ_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "퀴즈 번호는 중복될 수 없습니다."),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 퀴즈를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
