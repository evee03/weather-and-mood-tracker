package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.weather_mood_tracker.model.enums.WeatherType;

import java.time.LocalDate;

@Entity
@Table(name = "weathers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private WeatherType weatherType;
}
