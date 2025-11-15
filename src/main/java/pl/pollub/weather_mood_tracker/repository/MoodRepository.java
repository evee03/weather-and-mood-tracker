package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.Mood;
import pl.pollub.weather_mood_tracker.model.User;

import java.time.LocalDate;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    boolean existsByUserAndDate(User user, LocalDate date);
}