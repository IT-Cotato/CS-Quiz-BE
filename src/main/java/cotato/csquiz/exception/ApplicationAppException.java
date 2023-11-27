package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApplicationAppException extends IllegalArgumentException {
	private ErrorCode errorCode;
	private String message;
}
