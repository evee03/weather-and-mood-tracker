package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.PhysicalActivity;

@Repository
public interface PhysicalActivityRepository extends JpaRepository<PhysicalActivity, Long> {

}