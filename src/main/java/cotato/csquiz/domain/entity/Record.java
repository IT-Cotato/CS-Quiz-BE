package cotato.csquiz.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_reply")
    private String reply;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Column(name = "record_is_correct")
    private Boolean isCorrect;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

}
