package cotato.csquiz.domain.dto.education;

public record UpdateEducationRequest(
        Long educationId,
        String newSubject,
        int newNumber
) {
}
