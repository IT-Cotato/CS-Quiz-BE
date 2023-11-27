package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "session_num")
    private int number;

    @Column(name = "session_photo_url")
    private String photoUrl;

    @Column(name = "session_description")
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "generation_id")
    private Generation generation;

    @Builder
    public Session(int number, String photoUrl, String description, Generation generation) {
        this.number =number;
        this.photoUrl=photoUrl;
        this.description=description;
        this.generation=generation;
    }
    public int changeSessionNum(int sessionNum){
        this.number = sessionNum;
        return this.number;
    }
    public long changeDescription(String description) {
        this.description = description;
        return this.id;
    }
    public long changePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this.id;
    }
}
