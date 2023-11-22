package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "존재하는 이메일 입니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "존재하는 전화번호입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
