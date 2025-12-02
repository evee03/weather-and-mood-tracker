package pl.pollub.weather_mood_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.pollub.weather_mood_tracker.model.converter.MapToJsonConverter;
import pl.pollub.weather_mood_tracker.model.enums.WeatherType;

import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "avg_mood_per_weather", columnDefinition = "jsonb")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Double> avgMoodPerWeather;

    @Enumerated(EnumType.STRING)
    @Column(name = "best_weather_type")
    private WeatherType bestWeatherType;

    @Column(name = "streak_days")
    private Integer streakDays;

    @Column(name = "avg_water_intake")
    private Double avgWaterIntake;

    @Column(name = "avg_activity_time")
    private Double avgActivityTime;

    @Column(name = "avg_city_mood")
    private Double avgCityMood;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stats)) return false;
        Stats stats = (Stats) o;
        return id != null && Objects.equals(id, stats.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}