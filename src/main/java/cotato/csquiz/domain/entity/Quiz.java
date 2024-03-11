package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.domain.enums.QuizType;
import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @Column(name = "quiz_number", nullable = false)
    private int number;

    @Column(name = "quiz_question")
    private String question;

    @Column(name = "quiz_photo_url")
    private String photoUrl;

    @Column(name = "quiz_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'QUIZ_OFF'")
    private QuizStatus status;

    @Column(name = "quiz_start")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'QUIZ_OFF'")
    private QuizStatus start;

    @Column(name = "quiz_appear_second")
    private int appearSecond;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "generation_id")
    private Generation generation;

    @OneToMany(mappedBy = "quiz", orphanRemoval = true)
    @JsonIgnore
    private List<Record> records = new ArrayList<>();

    @OneToOne(mappedBy = "quiz", orphanRemoval = true)
    @JsonIgnore
    private Scorer scorer;

    public Quiz(int number, String question, String photoUrl, Education education, int appearSecond,
                Generation generation) {
        this.number = number;
        this.question = question;
        this.photoUrl = photoUrl;
        this.education = education;
        this.appearSecond = appearSecond;
        this.generation = generation;
    }

    public void updateStatus(QuizStatus status) {
        this.status = status;
    }

    public void updateStart(QuizStatus status) {
        this.start = status;
    }

    public boolean isOff() {
        return status == QuizStatus.QUIZ_OFF;
    }

    public boolean isStart() {
        return start == QuizStatus.QUIZ_ON;
    }

    public QuizType getQuizType() {
        if (this instanceof MultipleQuiz) {
            return QuizType.MULTIPLE_QUIZ;
        }
        return QuizType.SHORT_QUIZ;
    }
}
