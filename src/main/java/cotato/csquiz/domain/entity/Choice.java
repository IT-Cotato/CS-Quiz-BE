package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    @Column(name = "choice_number")
    private int choiceNumber;

    @Column(name = "choice_content")
    private String content;

    @Column(name = "choice_correct")
    @Enumerated(EnumType.STRING)
    private ChoiceCorrect isCorrect;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private MultipleQuiz multipleQuiz;
}
