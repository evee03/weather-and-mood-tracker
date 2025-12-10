package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.pollub.weather_mood_tracker.auth.UserService;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.Weather;
import pl.pollub.weather_mood_tracker.util.AppConstants;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserService userService;
    private final WeatherService weatherService;
    private final QuoteService quoteService;

    public void prepareDashboardData(Model model, Long userId) {
        User user = userService.findById(userId).orElseThrow();
        model.addAttribute("user", user);

        String langCode = user.getSettings() != null ? user.getSettings().getPreferredLanguage().name().toLowerCase() : AppConstants.DEFAULT_LANG;
        String theme = user.getSettings() != null ? user.getSettings().getTheme().name().toLowerCase() : "light";

        model.addAttribute("userTheme", theme);
        model.addAttribute("userLanguage", langCode);

        model.addAttribute("dailyQuote", quoteService.getQuoteOfDay(langCode));

        Weather currentWeather = weatherService.getOrCreateWeather(user.getCity());
        model.addAttribute("currentWeather", currentWeather);
        model.addAttribute("weatherAdvice", getWeatherAdvice(currentWeather.getWeatherType().name(), langCode));

        model.addAttribute("usersInCity", userService.countUsersInCity(user.getCity()));
        model.addAttribute("vibeToday", userService.getCityAverageMoodForDate(user.getCity(), LocalDate.now()));
        model.addAttribute("vibeYesterday", userService.getCityAverageMoodForDate(user.getCity(), LocalDate.now().minusDays(1)));
    }

    private String getWeatherAdvice(String weatherType, String lang) {
        boolean isPl = "pl".equalsIgnoreCase(lang);
        return switch (weatherType) {
            case "RAINY", "STORMY" -> isPl ? "Zaparz herbatę i odpocznij w domu" : "Make some tea and relax at home";
            case "SUNNY" -> isPl ? "Idealny czas na spacer i witaminę D!" : "Perfect time for a walk and some Vitamin D!";
            case "SNOWY" -> isPl ? "Może ulepimy bałwana?" : "Do you want to build a snowman?";
            case "CLOUDY" -> isPl ? "Dobra pogoda na skupienie i pracę" : "Good weather for focus and work";
            default -> isPl ? "Pamiętaj o nawodnieniu niezależnie od pogody" : "Remember to stay hydrated regardless of weather";
        };
    }
}