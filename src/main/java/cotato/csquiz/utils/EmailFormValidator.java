package cotato.csquiz.utils;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class EmailFormValidator {

    private static final String NAVER_REGEX = "^[a-zA-Z0-9]+@naver\\.com$";
    private static final String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";

    public void validateEmailForm(String email) {
        if (!email.matches(NAVER_REGEX) && !email.matches(GMAIL_REGEX)) {
            throw new AppException(ErrorCode.EMAIL_TYPE_ERROR);
        }
    }
}
