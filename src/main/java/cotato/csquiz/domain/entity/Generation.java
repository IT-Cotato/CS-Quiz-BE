package cotato.csquiz.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
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
}
