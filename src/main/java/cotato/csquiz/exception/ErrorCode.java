package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	//기수 관련 에러 코드
	GENERATION_NOTFOUND(HttpStatus.NOT_FOUND, "");


	private HttpStatus httpStatus;
	private String message;
}
