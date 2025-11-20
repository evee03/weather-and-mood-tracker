package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.dto.ActivityEntryDto;
import pl.pollub.weather_mood_tracker.model.PhysicalActivity;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.enums.ActivityType;
import pl.pollub.weather_mood_tracker.repository.PhysicalActivityRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;
import java.util.List;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PhysicalActivityService {

    private final PhysicalActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addActivity(Long userId, ActivityEntryDto dto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        LocalDate today = LocalDate.now();

        List<ActivityType> selectedActivities = dto.getActivityTypes();

        for (ActivityType type : selectedActivities) {
            PhysicalActivity activity = PhysicalActivity.builder()
                    .user(user)
                    .date(today)
                    .activityType(type)
                    .build();

            activityRepository.save(activity);
        }
    }
}