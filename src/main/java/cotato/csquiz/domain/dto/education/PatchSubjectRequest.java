package cotato.csquiz.domain.dto.education;

import lombok.Getter;

@Getter
public class PatchSubjectRequest {
    private long educationId;
    private String newSubject;
}
