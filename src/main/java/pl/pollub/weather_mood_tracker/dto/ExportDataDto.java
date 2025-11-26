package pl.pollub.weather_mood_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.weather_mood_tracker.model.Hydration;
import pl.pollub.weather_mood_tracker.model.Mood;
import pl.pollub.weather_mood_tracker.model.PhysicalActivity;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Mood> moods;
    private List<Hydration> hydrations;
    private List<PhysicalActivity> activities;
}