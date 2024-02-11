package cotato.csquiz.domain.dto.session;

import cotato.csquiz.domain.entity.Session;

public record CsEducationOnSessionNumberResponse (
    Long sessionId,
    int sessionNumber
){
    public static CsEducationOnSessionNumberResponse from(Session session){
        return new CsEducationOnSessionNumberResponse(
                session.getId(),
                session.getNumber()
        );
    }
}
