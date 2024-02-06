package cotato.csquiz.domain.dto.quiz;

import cotato.csquiz.domain.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {

    private Long id;
    private int number;
    private QuizType type;
    private String question;
    private String photoUrl;
}
