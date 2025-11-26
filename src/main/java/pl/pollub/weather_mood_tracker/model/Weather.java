package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import lombok.*;
import pl.pollub.weather_mood_tracker.model.enums.WeatherType;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "weathers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer humidity;

    @Column(nullable = false)
    private Integer pressure;

    @Column(name = "weather_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private WeatherType weatherType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weather)) return false;
        Weather weather = (Weather) o;
        return id != null && Objects.equals(id, weather.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}