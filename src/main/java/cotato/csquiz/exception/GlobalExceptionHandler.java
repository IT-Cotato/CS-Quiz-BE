package cotato.csquiz.exception;

import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<?> handleAppCustomException(AppException e, HttpServletRequest request) {
        log.error("AppCustomException 발생: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                e.getErrorCode().getHttpStatus(),
                e.getErrorCode(),
                e.getErrorCode().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<?> handleImageException(ImageException e, HttpServletRequest request) {
        log.error("이미지 처리 실패 예외 발생: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.IMAGE_PROCESSING_FAIL,
                "이미지 처리에 실패했습니다.",
                request.getRequestURI()
        );
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error("Entity Not Found Exception 발생: {}", e.getMessage());
        log.error("에러 요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                ErrorCode.ENTITY_NOT_FOUND,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("발생한 에러: {}", e.getErrorCode());
        log.error("에러 요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SQL_ERROR,
                ErrorCode.INTERNAL_SQL_ERROR.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }
}
