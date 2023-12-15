package cotato.csquiz.domain.dto.email;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class SendEmailRequest {

    @Email
    private String email;
}
