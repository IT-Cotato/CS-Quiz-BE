package cotato.csquiz.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "MultipleQuiz")
public class MultipleQuiz extends Quiz {

    @OneToMany(mappedBy = "multipleQuiz", cascade = CascadeType.ALL)
    private List<Choice> choices;

    @Builder
    public MultipleQuiz(Integer number, String question, String photoUrl, Education education, int appearSecond,
                        Generation generation) {
        super(number, question, photoUrl, education, appearSecond, generation);
    }

}
