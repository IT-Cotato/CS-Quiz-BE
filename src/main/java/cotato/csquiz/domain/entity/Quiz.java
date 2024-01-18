package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@DynamicInsert
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

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
    @ColumnDefault(value = "'OFF'")
    private QuizStatus status;

    @Column(name = "quiz_start")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'OFF'")
    private QuizStatus start;

    @Column(name = "quiz_appear_second")
    private int appearSecond;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    public Quiz(int number, String question, String photoUrl, Education education, int appearSecond) {
        this.number = number;
        this.question = question;
        this.photoUrl = photoUrl;
        this.education = education;
        this.appearSecond = appearSecond;
    }
}
