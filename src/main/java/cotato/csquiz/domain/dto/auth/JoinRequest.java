package cotato.csquiz.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinRequest {

    @Email
    private String email;
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야합니다.")
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    @Size(min = 11, max = 11, message = "전화번호는 11자리여야합니다.")
    private String phoneNumber;
}
