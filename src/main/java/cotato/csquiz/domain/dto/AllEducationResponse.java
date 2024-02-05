package cotato.csquiz.domain.dto;

import cotato.csquiz.domain.entity.Education;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllEducationResponse {
    private Long educationId;
    private String subject;
    private int educationNumber;

    public static AllEducationResponse convertFromEducation(Education education) {
        return AllEducationResponse.builder()
                .educationId(education.getId())
                .subject(education.getSubject())
                .educationNumber(education.getNumber())
                .build();
    }
}