package cotato.csquiz.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record AppExceptionResponse(
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
}
