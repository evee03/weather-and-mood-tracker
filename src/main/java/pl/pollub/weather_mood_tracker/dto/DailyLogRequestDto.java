package pl.pollub.weather_mood_tracker.dto;

import lombok.Data;
import pl.pollub.weather_mood_tracker.model.enums.ActivityType;
import java.util.List;

@Data
public class DailyLogRequestDto {
    private Integer moodLevel;
    private String moodComment;
    private Integer hydrationMl;
    private List<ActivityType> activities;
    private String date;
}