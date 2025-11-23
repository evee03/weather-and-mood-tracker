package pl.pollub.weather_mood_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.pollub.weather_mood_tracker.model.Mood;
import pl.pollub.weather_mood_tracker.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    boolean existsByUserAndDate(User user, LocalDate date);
    Optional<Mood> findByUserAndDate(User user, LocalDate date);
    List<Mood> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Mood> findAllByDateBetween(LocalDate start, LocalDate end); // Do statystyk ogólnych

    // Pobiera średnią nastroju w danym mieście dla KONKRETNEJ DATY
    @Query("SELECT AVG(m.moodLevel) FROM Mood m JOIN m.user u " +
            "WHERE u.city = :city AND m.date = :date")
    Double findAverageMoodByCityAndDate(@Param("city") String city, @Param("date") LocalDate date);

    // Do wykresu porównawczego (Ja vs Miasto - dzień po dniu)
    @Query("SELECT m.date, AVG(m.moodLevel) FROM Mood m JOIN m.user u " +
            "WHERE u.city = :city AND m.date BETWEEN :start AND :end GROUP BY m.date")
    List<Object[]> findDailyAverageMoodByCity(@Param("city") String city,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end);
}