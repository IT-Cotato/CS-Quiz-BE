package cotato.csquiz.entity;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class Scorer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scorer_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
