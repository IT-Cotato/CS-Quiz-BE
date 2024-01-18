package cotato.csquiz.domain.dto.quiz;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateShortQuizRequest {

    private int number;
    private String question;
    private MultipartFile image;
    private List<CreateShortAnswerRequest> shortAnswers;
}
