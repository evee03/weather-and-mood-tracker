package pl.pollub.weather_mood_tracker.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import pl.pollub.weather_mood_tracker.model.enums.ActivityType;
import java.util.List;

@Data
public class ActivityEntryDto {
    @NotEmpty(message = "{activity.type.required}")
    private List<ActivityType> activityTypes;
}