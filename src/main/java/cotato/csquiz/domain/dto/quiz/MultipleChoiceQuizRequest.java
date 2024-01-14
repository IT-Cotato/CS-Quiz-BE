package cotato.csquiz.domain.dto.quiz;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuizRequest {

    private int number;
    private String question;
    private MultipartFile quizImage;
    private List<CreateChoiceRequest> choices;
}
