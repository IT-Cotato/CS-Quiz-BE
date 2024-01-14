package cotato.csquiz.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MultipleQuiz extends Quiz {

    @OneToMany(mappedBy = "multipleQuiz", cascade = CascadeType.ALL)
    private List<Choice> choices = new ArrayList<>();
}
