package pl.pollub.weather_mood_tracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoodEntryDto {

    @NotNull
    @Min(value = 1, message = "Mood level must be at least 1")
    @Max(value = 5, message = "Mood level must be at most 5")
    private Integer moodLevel;

    private String comment;
}