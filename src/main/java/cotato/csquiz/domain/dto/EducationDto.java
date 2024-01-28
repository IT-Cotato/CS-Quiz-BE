package cotato.csquiz.domain.dto;

import cotato.csquiz.domain.entity.Education;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationDto {
    private Long educationId;
    private String subject;
    private int educationNumber;

    public static EducationDto convertFromEducation(Education education) {
        return EducationDto.builder()
                .educationId(education.getId())
                .subject(education.getSubject())
                .educationNumber(education.getNumber())
                .build();
    }
}