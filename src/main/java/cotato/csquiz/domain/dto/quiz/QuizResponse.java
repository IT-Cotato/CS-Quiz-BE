package cotato.csquiz.domain.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {

    private Long id;
    private int number;
    private String question;
    private String image;
}
