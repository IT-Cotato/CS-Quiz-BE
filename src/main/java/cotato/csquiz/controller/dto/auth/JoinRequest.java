package cotato.csquiz.controller.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JoinRequest(
        @Email
        @NotNull
        String email,
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야합니다.")
        @NotNull
        String password,
        @NotNull
        String name,
        @NotNull
        @Size(min = 11, max = 11, message = "전화번호는 11자리여야합니다.")
        String phoneNumber
) {
}
