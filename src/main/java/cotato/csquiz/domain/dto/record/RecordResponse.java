package cotato.csquiz.domain.dto.record;

import cotato.csquiz.domain.entity.Record;
import java.io.Serializable;

public record RecordResponse(
        Long recordId,
        Long ticketNumber,
        Long memberId,
        String memberName,
        String backFourNumber,
        String reply
) implements Serializable {

    public static RecordResponse from(Record record, String backFourNumber) {
        return new RecordResponse(
                record.getId(),
                record.getTicketNumber(),
                record.getMember().getId(),
                record.getMember().getName(),
                backFourNumber,
                record.getReply()
        );
    }
}
