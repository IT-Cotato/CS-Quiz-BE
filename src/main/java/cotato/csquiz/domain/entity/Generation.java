package cotato.csquiz.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Generation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generation_id")
    private Long id;

    @Column(name = "generation_name", unique = true, nullable = false)
    private String name;

    @Column(name = "generation_start_date")
    private LocalDate startDate;

    @Column(name = "generation_end_date")
    private LocalDate endDate;

    @Column(name = "generation_recruiting")
    private Boolean isRecruit;

    @Builder
    public Generation(String name, LocalDate startDate, LocalDate endDate){
        this.name=name;
        this.startDate=startDate;
        this.endDate=endDate;
        isRecruit=false;
    }

    public long changeRecruit(boolean isRecruit){
        this.isRecruit = isRecruit;
        return this.getId();
    }

    public long changePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate=startDate;
        this.endDate = endDate;
        return this.getId();
    }
}
