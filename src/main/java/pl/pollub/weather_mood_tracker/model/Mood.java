package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "moods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "weather_id", nullable = false)
    private Weather weather;

    @Column(name = "mood_level", nullable = false)
    @Min(1)
    @Max(5)
    private Integer moodLevel;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    private LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mood)) return false;
        Mood mood = (Mood) o;
        return id != null && Objects.equals(id, mood.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}