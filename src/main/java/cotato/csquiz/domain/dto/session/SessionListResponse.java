package cotato.csquiz.domain.dto.session;

import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.domain.enums.ItIssue;
import cotato.csquiz.domain.enums.Networking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionListResponse {
    private Long sessionId;
    private int sessionNumber;
    private String photoUrl;
    private String description;
    private Long generationId;
    private ItIssue itIssue;
    private Networking networking;
    private CSEducation csEducation;

    public static SessionListResponse from(Session session) {
        return SessionListResponse.builder()
                .sessionId(session.getId())
                .sessionNumber(session.getNumber())
                .photoUrl(session.getPhotoUrl())
                .description(session.getDescription())
                .generationId(session.getGeneration().getId())
                .itIssue(session.getItIssue())
                .networking(session.getNetworking())
                .csEducation(session.getCsEducation())
                .build();
    }
}
