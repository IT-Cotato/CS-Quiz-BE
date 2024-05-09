package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @Column(name = "education_number")
    private Integer number;

    @Column(name = "education_subject")
    private String subject;

    @Column(name = "education_status")
    @Enumerated(EnumType.STRING)
    private EducationStatus status;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(name = "generation_id")
    private Long generationId;

    @Builder
    public Education(String subject, Integer educationNumber, Session session, Long generationId) {
        this.subject = subject;
        this.number = educationNumber;
        this.session = session;
        this.generationId = generationId;
        status = EducationStatus.BEFORE;
    }

    public void updateStatus(EducationStatus status) {
        this.status = status;
    }

    public void updateSubject(String newSubject) {
        this.subject = newSubject;
    }

    public void updateNumber(Integer changeNumber) {
        this.number = changeNumber;
    }
}
