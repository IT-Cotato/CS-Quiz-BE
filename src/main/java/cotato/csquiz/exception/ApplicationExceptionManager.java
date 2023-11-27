package cotato.csquiz.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ApplicationExceptionManager {
	@ExceptionHandler(ApplicationAppException.class)
	public ResponseEntity<?> illegalArgumentExceptionHandler(ApplicationAppException e){
		return ResponseEntity.status(e.getErrorCode().getHttpStatus())
			.body(e.getErrorCode() + " " + e.getMessage());
	}
}
