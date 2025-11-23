package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.dto.DailyLogRequestDto;
import pl.pollub.weather_mood_tracker.model.*;
import pl.pollub.weather_mood_tracker.repository.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final UserRepository userRepository;
    private final MoodRepository moodRepository;
    private final HydrationRepository hydrationRepository;
    private final PhysicalActivityRepository activityRepository;
    private final WeatherService weatherService;

    @Transactional
    public void saveDailyLog(Long userId, DailyLogRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate date = LocalDate.parse(dto.getDate());
        LocalDate today = LocalDate.now();

        if (dto.getMoodLevel() != null) {
            Mood mood = moodRepository.findByUserAndDate(user, date)
                    .orElse(Mood.builder().user(user).date(date).build());

            if (mood.getWeather() == null) {
                if (date.equals(today)) {
                    Weather weather = weatherService.getOrCreateWeather(user.getCity());
                    mood.setWeather(weather);
                } else {
                    Weather weather = weatherService.getOrCreateWeather(user.getCity());
                    mood.setWeather(weather);
                }
            }

            mood.setMoodLevel(dto.getMoodLevel());
            mood.setComment(dto.getMoodComment());
            moodRepository.save(mood);
        }

        if (dto.getHydrationMl() != null) {
            Hydration hydration = hydrationRepository.findByUserAndDate(user, date)
                    .orElse(Hydration.builder().user(user).date(date).goalMl(2500).build());
            hydration.setWaterIntakeMl(dto.getHydrationMl());
            hydrationRepository.save(hydration);
        }

        if (dto.getActivities() != null) {
            List<PhysicalActivity> existing = activityRepository.findAllByUserAndDate(user, date);
            activityRepository.deleteAll(existing);

            for (var type : dto.getActivities()) {
                PhysicalActivity activity = PhysicalActivity.builder()
                        .user(user)
                        .date(date)
                        .activityType(type)
                        .build();
                activityRepository.save(activity);
            }
        }
    }

    @Transactional
    public void deleteDailyLog(Long userId, String dateStr) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate date = LocalDate.parse(dateStr);

        moodRepository.findByUserAndDate(user, date).ifPresent(moodRepository::delete);
        hydrationRepository.findByUserAndDate(user, date).ifPresent(hydrationRepository::delete);
        List<PhysicalActivity> acts = activityRepository.findAllByUserAndDate(user, date);
        activityRepository.deleteAll(acts);
    }

    public Set<String> getFilledDates(Long userId, LocalDate start, LocalDate end) {
        User user = userRepository.findById(userId).orElseThrow();

        Set<LocalDate> dates = new HashSet<>();

        moodRepository.findAllByUserAndDateBetween(user, start, end)
                .forEach(m -> dates.add(m.getDate()));

        hydrationRepository.findAllByUserAndDateBetween(user, start, end)
                .forEach(h -> dates.add(h.getDate()));

        activityRepository.findAllByUserAndDateBetween(user, start, end)
                .forEach(a -> dates.add(a.getDate()));

        return dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toSet());
    }
}