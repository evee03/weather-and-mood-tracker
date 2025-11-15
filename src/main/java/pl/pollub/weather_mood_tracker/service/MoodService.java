package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.config.Language;
import pl.pollub.weather_mood_tracker.dto.MoodEntryDto;
import pl.pollub.weather_mood_tracker.model.Mood;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.Weather;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final WeatherService weatherService;

    @Transactional
    public void saveMood(MoodEntryDto dto, Long userId, Locale locale) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        LocalDate today = LocalDate.now();

        if (moodRepository.existsByUserAndDate(user, today)) {
            Language language = new Language(locale);
            throw new Exception(language.getMessage("mood.save.already_exists"));
        }

        Weather weather = weatherService.getOrCreateWeather(user.getCity());

        Mood mood = Mood.builder()
                .user(user)
                .weather(weather)
                .moodLevel(dto.getMoodLevel())
                .comment(dto.getComment())
                .date(today)
                .build();

        moodRepository.save(mood);
    }
}