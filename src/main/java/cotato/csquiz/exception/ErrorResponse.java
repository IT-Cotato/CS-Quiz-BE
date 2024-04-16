package cotato.csquiz.exception;

import jakarta.servlet.http.HttpServletRequest;

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

    public static ErrorResponse of(HttpServletRequest request, ErrorCode errorCode, final String errorMessage) {
        return new ErrorResponse(
                errorCode,
                errorMessage,
                request.getMethod(),
                request.getRequestURI()
        );
    }
}
