package cotato.csquiz.domain.dto.education;

import cotato.csquiz.domain.entity.Education;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EducationListResponse {

    private final List<EducationResponse> educations;
}