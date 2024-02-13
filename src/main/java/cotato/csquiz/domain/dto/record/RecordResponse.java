package cotato.csquiz.domain.dto.record;

import cotato.csquiz.domain.entity.Record;
import java.io.Serializable;

public record RecordResponse(
        Long recordId,
        Long ticketNumber,
        Long memberId,
        String memberName,
        String backForNumber,
        String reply
) implements Serializable {

    public static RecordResponse from(Record record) {
        return new RecordResponse(
                record.getId(),
                record.getTicketNumber(),
                record.getMember().getId(),
                record.getMember().getName(),
                record.getMember().getBackFourNumber(),
                record.getReply()
        );
    }
}
