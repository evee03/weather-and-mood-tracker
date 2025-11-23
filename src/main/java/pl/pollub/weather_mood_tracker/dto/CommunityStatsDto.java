package pl.pollub.weather_mood_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityStatsDto {

    @Data
    @AllArgsConstructor
    public static class DataPoint {
        private Number x;
        private Number y;
    }

    private List<DataPoint> moodVsHydration;
    private List<DataPoint> moodVsPressure;
    private List<DataPoint> moodVsHumidity;

    private Map<String, Double> myMoodHistory;
    private Map<String, Double> cityMoodHistory;
}