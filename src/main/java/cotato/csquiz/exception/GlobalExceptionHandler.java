package cotato.csquiz.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appCustomException(AppException e, HttpServletRequest request) {
        log.error("발생한 에러: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                e.getErrorCode().getHttpStatus(),
                e.getErrorCode().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<?> imageException(ImageException e, HttpServletRequest request) {
        log.error("발생한 에러: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode() + " " + e.getErrorCode().getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> sqlException(SQLException e, HttpServletRequest request) {
        log.error("발생한 에러: {}", e.getErrorCode());
        log.error("에러 요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SQL Error",
                request.getRequestURI()
        );
        return ResponseEntity.status(500).body(errorResponse);
    }
}
