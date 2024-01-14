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
public class ShortQuiz extends Quiz {

    @OneToMany(mappedBy = "shortQuiz", cascade = CascadeType.ALL)
    private List<ShortAnswer> shortAnswers = new ArrayList<>();

    public void addShortAnswers(List<ShortAnswer> shortAnswers) {
        this.shortAnswers.addAll(shortAnswers);
        shortAnswers.forEach(shortAnswer -> shortAnswer.matchShortQuiz(this));
    }

    @Builder
    public ShortQuiz(int number, String question, String photoUrl, Education education, int appearSecond) {
        super(number, question, photoUrl, education, appearSecond);
    }
}
