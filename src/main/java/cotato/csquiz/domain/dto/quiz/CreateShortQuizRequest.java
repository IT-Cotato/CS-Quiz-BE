package cotato.csquiz.domain.dto.quiz;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShortQuizRequest {

    private int number;
    private String question;
    private MultipartFile image;
    private List<CreateShortAnswerRequest> shortAnswers;
}
