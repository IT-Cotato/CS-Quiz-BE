package cotato.csquiz.controller.dto.education;

public record UpdateEducationRequest(
        Long educationId,
        String newSubject,
        int newNumber
) {
}
