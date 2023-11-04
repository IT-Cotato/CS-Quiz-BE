package cotato.csquiz.entity;

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
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @Column(name = "quiz_number")
    private int number;

    @Column(name = "quiz_question")//todo
    private String question;

    @Column(name = "quiz_type")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "MULTIPLE_CHOICES")
    private QuizType type;

    @Column(name = "quiz_photo_url")
    private String photoUrl;

    @Column(name = "quiz_status")
    @Enumerated(EnumType.STRING)
    private QuizStatus status;

    @Column(name = "quiz_appear_second")
    private int appearSecond;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Eduction eduction;

}
