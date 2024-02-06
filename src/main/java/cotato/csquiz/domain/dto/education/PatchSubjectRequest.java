package cotato.csquiz.domain.dto.education;

import lombok.Getter;

@Getter
public class PatchSubjectRequest {
    private Long educationId;
    private String newSubject;
}
