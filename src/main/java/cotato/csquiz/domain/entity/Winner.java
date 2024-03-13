package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Winner extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "winner_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    public Winner(Member member, Education education) {
        Objects.requireNonNull(member);
        Objects.requireNonNull(education);

        this.member = member;
        this.education = education;
    }

    public static Winner of(Member member, Education education) {
        return new Winner(
                member,
                education
        );
    }
}
