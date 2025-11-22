package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.dto.HydrationEntryDto;
import pl.pollub.weather_mood_tracker.model.Hydration;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.HydrationRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HydrationService {

    private final HydrationRepository hydrationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addHydration(Long userId, HydrationEntryDto dto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        LocalDate today = LocalDate.now();

        Hydration hydration = hydrationRepository.findByUserAndDate(user, today)
                .orElse(Hydration.builder()
                        .user(user)
                        .date(today)
                        .waterIntakeMl(0)
                        .goalMl(2500)
                        .build());

        hydration.setWaterIntakeMl(hydration.getWaterIntakeMl() + dto.getAmountMl());

        hydrationRepository.save(hydration);
    }
}