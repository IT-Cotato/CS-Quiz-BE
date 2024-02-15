package cotato.csquiz.domain.dto.member;

public record MemberEmailResponse(
        String email
) {
    public static MemberEmailResponse from(String email) {
        return new MemberEmailResponse(email);
    }
}
