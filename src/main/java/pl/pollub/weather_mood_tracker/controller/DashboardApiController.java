package pl.pollub.weather_mood_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.weather_mood_tracker.dto.CommunityStatsDto;
import pl.pollub.weather_mood_tracker.dto.DailyLogRequestDto;
import pl.pollub.weather_mood_tracker.dto.DailyLogResponse;
import pl.pollub.weather_mood_tracker.model.*;
import pl.pollub.weather_mood_tracker.repository.*;
import pl.pollub.weather_mood_tracker.service.DailyLogService;
import pl.pollub.weather_mood_tracker.service.StatisticsService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final MoodRepository moodRepository;
    private final HydrationRepository hydrationRepository;
    private final PhysicalActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final DailyLogService dailyLogService;
    private final StatisticsService statisticsService;

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
            Weather w = mood.get().getWeather();
            responseBuilder.temperature(w.getTemperature());
            responseBuilder.weatherType(w.getWeatherType());
            responseBuilder.humidity(w.getHumidity());
            responseBuilder.pressure(w.getPressure());
            responseBuilder.city(w.getCity());
        }

        return ResponseEntity.ok(responseBuilder.build());
    }

    @PostMapping("/log")
    public ResponseEntity<?> saveDailyLog(@RequestBody DailyLogRequestDto dto,
                                          @SessionAttribute("userId") Long userId) {
        try {
            dailyLogService.saveDailyLog(userId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/log/{date}")
    public ResponseEntity<?> deleteDailyLog(@PathVariable String date,
                                            @SessionAttribute("userId") Long userId) {
        dailyLogService.deleteDailyLog(userId, date);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/community")
    public ResponseEntity<CommunityStatsDto> getCommunityStats(@SessionAttribute("userId") Long userId) {
        CommunityStatsDto stats = statisticsService.calculateStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/calendar/events")
    public ResponseEntity<List<Map<String, Object>>> getCalendarEvents(
            @RequestParam String start,
            @RequestParam String end,
            @SessionAttribute("userId") Long userId) {

        if (userId == null) return ResponseEntity.status(401).build();

        LocalDate startDate = LocalDate.parse(start.split("T")[0]);
        LocalDate endDate = LocalDate.parse(end.split("T")[0]);

        Set<String> filledDates = dailyLogService.getFilledDates(userId, startDate, endDate);

        List<Map<String, Object>> events = filledDates.stream().map(date -> {
            Map<String, Object> event = new HashMap<>();
            event.put("start", date);
            event.put("display", "background");
            event.put("backgroundColor", "rgba(25, 135, 84, 0.15)");
            event.put("classNames", List.of("filled-day-bg"));

            return event;
        }).collect(Collectors.toList());

        List<Map<String, Object>> icons = filledDates.stream().map(date -> {
            Map<String, Object> event = new HashMap<>();
            event.put("start", date);
            event.put("title", "✅");
            event.put("allDay", true);
            event.put("classNames", List.of("filled-day-icon"));
            event.put("backgroundColor", "transparent");
            event.put("borderColor", "transparent");
            event.put("textColor", "#198754");
            return event;
        }).collect(Collectors.toList());

        events.addAll(icons);

        return ResponseEntity.ok(events);
    }
}