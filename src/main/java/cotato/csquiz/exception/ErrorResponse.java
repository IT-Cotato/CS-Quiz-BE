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

    public static ErrorResponse of(HttpStatus status, String requestURI, final BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder message = new StringBuilder();
        boolean isFirst = true;
        for (FieldError fieldError : fieldErrors) {
            if (!isFirst) {
                message.append(", ");
            }
            isFirst = false;
            String field = fieldError.getField();
            message.append("[")
                    .append(field)
                    .append("]");
        }
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT,
                message.toString(),
                requestURI
        );
    }
}
