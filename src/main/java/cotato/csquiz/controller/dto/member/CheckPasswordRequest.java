package cotato.csquiz.controller.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CheckPasswordRequest {

    @NotNull
    private String password;
}
