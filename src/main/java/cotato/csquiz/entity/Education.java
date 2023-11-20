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
import jakarta.persistence.OneToOne;

@Entity
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @Column(name = "education_subject")
    private String subject;

    @Column(name = "education_status")
    @Enumerated(EnumType.STRING)
    private EducationStatus status;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member winner;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

}
