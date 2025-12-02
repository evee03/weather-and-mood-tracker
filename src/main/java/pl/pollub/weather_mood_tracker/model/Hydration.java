package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "hydration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Hydration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Min(0)
    @Column(name = "water_intake_ml", nullable = false)
    private Integer waterIntakeMl;

    @Min(0)
    @Column(name = "goal_ml", nullable = false)
    private Integer goalMl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hydration)) return false;
        Hydration hydration = (Hydration) o;
        return id != null && Objects.equals(id, hydration.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}