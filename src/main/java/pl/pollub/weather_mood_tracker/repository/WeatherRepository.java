package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.Weather;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByCityAndDate(String city, LocalDate date);
}