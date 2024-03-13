package cotato.csquiz.controller.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinRequest {

    @Email
    private String email;
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야합니다.")
    private String password;
    @NotNull
    private String name;
    private String phoneNumber;
}
