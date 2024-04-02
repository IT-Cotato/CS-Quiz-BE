package cotato.csquiz.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppCustomException(AppException e, HttpServletRequest request) {
        log.error("AppException 발생: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), request);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> handleImageException(ImageException e, HttpServletRequest request) {
        log.error("이미지 처리 실패 예외 발생: {}", e.getErrorCode().getMessage());
        log.error("요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.IMAGE_PROCESSING_FAIL, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
        String requestURI = httpServletRequest.getRequestURI();
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorFields = bindingResult.getFieldErrors().stream()
                .map(FieldError::getField)
                .toList();

        log.error("[Method Argument Not Valid Execption 발생]: {}", errorFields);
        log.error("에러 발생 요청 경로: {}", requestURI);
        log.error("에러 발생 요청 메서드: {}", httpServletRequest.getMethod());

        ErrorResponse errorResponse = ErrorResponse.of(httpServletRequest, errorFields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e,
                                                                       HttpServletRequest request) {
        log.error("Entity Not Found Exception 발생: {}", e.getMessage());
        log.error("에러 요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("발생한 에러: {}", e.getErrorCode());
        log.error("에러 요청 uri: {}", request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SQL_ERROR, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
