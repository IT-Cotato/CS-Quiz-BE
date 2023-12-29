package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "존재하는 이메일 입니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "존재하는 전화번호입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인 요청에 실패했습니다."),
    JWT_NOT_EXISTS(HttpStatus.NO_CONTENT, "Jwt 토큰이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "요청하신 코드가 일치하지 않습니다."),
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "구글, 네이버 형식으로 입력해주세요"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    CREATE_VERIFY_CODE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "코드 생성에 실패했습니다."),
    NAME_NOT_MATCH(HttpStatus.NOT_FOUND, "해당 유저의 전화번호와 이름이 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "이전과 다른 비밀번호로 변경해주세요.");


    private final HttpStatus httpStatus;
    private final String message;
}
