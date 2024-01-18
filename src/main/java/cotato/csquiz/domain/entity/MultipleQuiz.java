package cotato.csquiz.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultipleQuiz extends Quiz {

    @OneToMany(mappedBy = "multipleQuiz", cascade = CascadeType.ALL)
    private List<Choice> choices = new ArrayList<>();

    @Builder
    public MultipleQuiz(int number, String question, String photoUrl, Education education, int appearSecond) {
        super(number, question, photoUrl, education, appearSecond);
    }

    public void addChoices(List<Choice> choices) {
        this.choices.addAll(choices);
        choices.forEach(choice -> choice.matchMultipleQuiz(this));
    }
}
