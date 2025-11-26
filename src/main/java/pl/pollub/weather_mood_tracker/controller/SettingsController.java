package pl.pollub.weather_mood_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.weather_mood_tracker.dto.ExportDataDto;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.HydrationRepository;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.PhysicalActivityRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserRepository userRepository;
    private final MoodRepository moodRepository;
    private final HydrationRepository hydrationRepository;
    private final PhysicalActivityRepository activityRepository;

    @GetMapping("/export")
    public ResponseEntity<ExportDataDto> exportData(
            @SessionAttribute("userId") Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String format
    ) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate today = LocalDate.now();

        if (to.isAfter(today)) {
            to = today;
        }
        if (from.isAfter(to)) {
            from = to;
        }

        ExportDataDto data = ExportDataDto.builder()
                .startDate(from)
                .endDate(to)
                .moods(moodRepository.findAllByUserAndDateBetween(user, from, to))
                .hydrations(hydrationRepository.findAllByUserAndDateBetween(user, from, to))
                .activities(activityRepository.findAllByUserAndDateBetween(user, from, to))
                .build();

        MediaType contentType = "xml".equalsIgnoreCase(format)
                ? MediaType.APPLICATION_XML
                : MediaType.APPLICATION_JSON;

        String filename = "tracker-export-" + from + "-to-" + to + "." + format.toLowerCase();

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }
}