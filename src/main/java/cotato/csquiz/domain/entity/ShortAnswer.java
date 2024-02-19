package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ShortAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "short_answer_id")
    private Long id;

    @Column(name = "short_answer_content")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private ShortQuiz shortQuiz;

    public void matchShortQuiz(ShortQuiz shortQuiz) {
        this.shortQuiz = shortQuiz;
    }

    @Builder
    public ShortAnswer(String content) {
        this.content = content;
    }

    public static ShortAnswer of(String content) {
        return new ShortAnswer(
                content
        );
    }
}
