package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.Hydration;
import pl.pollub.weather_mood_tracker.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HydrationRepository extends JpaRepository<Hydration, Long> {
    Optional<Hydration> findByUserAndDate(User user, LocalDate date);
    List<Hydration> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}