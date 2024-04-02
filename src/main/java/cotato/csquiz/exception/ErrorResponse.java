package cotato.csquiz.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public record ErrorResponse(
        @JsonFormat(
                shape = Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss",
                locale = "Asia/Seoul"
        )
        LocalDateTime time,
        HttpStatus status,
        ErrorCode errorCode,
        String message,
        String requestURI
) {

    public static ErrorResponse of(HttpStatus status, ErrorCode errorCode, String requestURI) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                errorCode,
                errorCode.getMessage(),
                requestURI
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
                LocalDateTime.now(),
                status,
                ErrorCode.INVALID_INPUT,
                message.toString(),
                requestURI
        );
    }
}
