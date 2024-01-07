package cotato.csquiz.domain.dto.generation;

import lombok.Getter;

@Getter
public class AddGenerationRequest {
    private String generationName;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;
}
