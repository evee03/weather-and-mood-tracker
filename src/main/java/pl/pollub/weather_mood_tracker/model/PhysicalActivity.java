package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.weather_mood_tracker.model.enums.ActivityType;

import java.time.LocalDate;

@Entity
@Table(name = "physical_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhysicalActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;
}
