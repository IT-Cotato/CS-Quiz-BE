package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.domain.enums.ItIssue;
import cotato.csquiz.domain.enums.Networking;
import cotato.csquiz.global.entity.BaseTimeEntity;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "session_number")
    private int number;

    @Column(name = "session_photo_url")
    private String photoUrl;

    @Column(name = "session_description")
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "generation_id")
    private Generation generation;

    @Column(name = "session_it_issue")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'IT_OFF'")
    private ItIssue itIssue;

    @Column(name = "session_networking")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'NW_OFF'")
    private Networking networking;

    @Column(name = "session_cs_education")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'CS_OFF'")
    private CSEducation csEducation;

    @Builder
    public Session(int number, String photoUrl, String description, Generation generation, ItIssue itIssue,
                   CSEducation csEducation, Networking networking) {
        this.number = number;
        this.photoUrl = photoUrl;
        this.description = description;
        this.generation = generation;
        this.itIssue = itIssue;
        this.csEducation = csEducation;
        this.networking = networking;
    }

    public int changeSessionNum(int sessionNumber) {
        this.number = sessionNumber;
        return this.number;
    }

    public long changeDescription(String description) {
        this.description = description;
        return this.id;
    }

    public void changePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void updateToggle(ItIssue itIssue, CSEducation csEducation, Networking networking) {
        this.itIssue = itIssue;
        this.csEducation = csEducation;
        this.networking = networking;
    }
}
