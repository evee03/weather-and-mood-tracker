package pl.pollub.weather_mood_tracker.dto;

import lombok.Builder;
import lombok.Data;
import pl.pollub.weather_mood_tracker.model.enums.ActivityType;
import pl.pollub.weather_mood_tracker.model.enums.WeatherType;

import java.util.List;

@Data
@Builder
public class DailyLogResponse {
    private Integer moodLevel;
    private String moodComment;
    private Integer hydrationMl;
    private List<ActivityType> activities;
    private Double temperature;
    private WeatherType weatherType;
}