package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.PhysicalActivity;
import pl.pollub.weather_mood_tracker.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhysicalActivityRepository extends JpaRepository<PhysicalActivity, Long> {
    List<PhysicalActivity> findAllByUserAndDate(User user, LocalDate date);
    List<PhysicalActivity> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}