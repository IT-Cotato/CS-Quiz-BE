package cotato.csquiz.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus status,
        ErrorCode errorCode,
        String message,
        String method,
        String requestURI
) {

    public static ErrorResponse of(ErrorCode errorCode, HttpServletRequest request) {
        return new ErrorResponse(
                errorCode.getHttpStatus(),
                errorCode,
                errorCode.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );
    }

    //method argument 예외에 사용
    public static ErrorResponse of(HttpServletRequest request, final List<String> errorFields) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT,
                errorFields.toString(),
                request.getMethod(),
                request.getRequestURI()
        );
    }
}
