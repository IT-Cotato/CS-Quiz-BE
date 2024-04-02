package cotato.csquiz.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public record ErrorResponse(
        ErrorCode errorCode,
        String message,
        String method,
        String requestURI
) {

    public static ErrorResponse of(ErrorCode errorCode, HttpServletRequest request) {
        return new ErrorResponse(
                errorCode,
                errorCode.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );
    }

    public static ErrorResponse of(HttpServletRequest request, final List<String> errorFields) {
        return new ErrorResponse(
                ErrorCode.INVALID_INPUT,
                errorFields.toString(),
                request.getMethod(),
                request.getRequestURI()
        );
    }
}
