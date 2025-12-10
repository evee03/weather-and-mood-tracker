package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.weather_mood_tracker.dto.CommunityStatsDto;
import pl.pollub.weather_mood_tracker.model.Hydration;
import pl.pollub.weather_mood_tracker.model.Mood;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.HydrationRepository;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MoodRepository moodRepository;
    private final HydrationRepository hydrationRepository;
    private final UserRepository userRepository;

    public CommunityStatsDto calculateStats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        List<Mood> myMoods = moodRepository.findAllByUserAndDateBetween(user, startDate, endDate);
        List<Hydration> myHydrations = hydrationRepository.findAllByUserAndDateBetween(user, startDate, endDate);

        List<Mood> allMoods = moodRepository.findAllByDateBetween(startDate, endDate);

        List<Hydration> allHydrations = hydrationRepository.findAllByDateBetween(startDate, endDate);

        List<CommunityStatsDto.DataPoint> moodVsHydration = new ArrayList<>();
        Map<Long, Map<LocalDate, Integer>> hydrationMap = new HashMap<>();
        for (Hydration h : allHydrations) {
            hydrationMap.computeIfAbsent(h.getUser().getId(), k -> new HashMap<>())
                    .put(h.getDate(), h.getWaterIntakeMl());
        }

        for (Mood m : allMoods) {
            Long uid = m.getUser().getId();
            if (hydrationMap.containsKey(uid) && hydrationMap.get(uid).containsKey(m.getDate())) {
                int water = hydrationMap.get(uid).get(m.getDate());
                moodVsHydration.add(new CommunityStatsDto.DataPoint(water, m.getMoodLevel()));
            }
        }

        List<CommunityStatsDto.DataPoint> moodVsPressure = new ArrayList<>();
        List<CommunityStatsDto.DataPoint> moodVsHumidity = new ArrayList<>();

        for (Mood m : allMoods) {
            if (m.getWeather() != null) {
                moodVsPressure.add(new CommunityStatsDto.DataPoint(m.getWeather().getPressure(), m.getMoodLevel()));
                moodVsHumidity.add(new CommunityStatsDto.DataPoint(m.getWeather().getHumidity(), m.getMoodLevel()));
            }
        }

        Map<String, Double> myMoodHistory = myMoods.stream()
                .sorted(Comparator.comparing(Mood::getDate))
                .collect(Collectors.toMap(
                        m -> m.getDate().toString(),
                        m -> (double) m.getMoodLevel(),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));

        List<Object[]> cityStats = moodRepository.findDailyAverageMoodByCity(user.getCity(), startDate, endDate);
        Map<String, Double> cityMoodHistory = new LinkedHashMap<>();
        for (Object[] row : cityStats) {
            String date = row[0].toString();
            Double avg = (Double) row[1];
            cityMoodHistory.put(date, avg);
        }

        return CommunityStatsDto.builder()
                .moodVsHydration(moodVsHydration)
                .moodVsPressure(moodVsPressure)
                .moodVsHumidity(moodVsHumidity)
                .myMoodHistory(myMoodHistory)
                .cityMoodHistory(cityMoodHistory)
                .build();
    }
}