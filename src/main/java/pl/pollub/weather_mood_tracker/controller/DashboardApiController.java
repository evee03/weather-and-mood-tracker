package pl.pollub.weather_mood_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.weather_mood_tracker.dto.DailyLogResponse;
import pl.pollub.weather_mood_tracker.model.*;
import pl.pollub.weather_mood_tracker.repository.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final MoodRepository moodRepository;
    private final HydrationRepository hydrationRepository;
    private final PhysicalActivityRepository activityRepository;
    private final UserRepository userRepository;

    @GetMapping("/log/{date}")
    public ResponseEntity<DailyLogResponse> getDailyLog(@PathVariable String date,
                                                        @SessionAttribute("userId") Long userId) {
        if (userId == null) return ResponseEntity.status(401).build();

        LocalDate localDate = LocalDate.parse(date);
        User user = userRepository.findById(userId).orElseThrow();
        Optional<Mood> mood = moodRepository.findByUserAndDate(user, localDate);
        Optional<Hydration> hydration = hydrationRepository.findByUserAndDate(user, localDate);
        List<PhysicalActivity> activities = activityRepository.findAllByUserAndDate(user, localDate);

        DailyLogResponse.DailyLogResponseBuilder responseBuilder = DailyLogResponse.builder()
                .moodLevel(mood.map(Mood::getMoodLevel).orElse(null))
                .moodComment(mood.map(Mood::getComment).orElse(null))
                .hydrationMl(hydration.map(Hydration::getWaterIntakeMl).orElse(0))
                .activities(activities.stream().map(PhysicalActivity::getActivityType).collect(Collectors.toList()));

        if (mood.isPresent() && mood.get().getWeather() != null) {
            responseBuilder.temperature(mood.get().getWeather().getTemperature());
            responseBuilder.weatherType(mood.get().getWeather().getWeatherType());
        }

        return ResponseEntity.ok(responseBuilder.build());
    }
}