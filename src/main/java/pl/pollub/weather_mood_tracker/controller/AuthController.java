package pl.pollub.weather_mood_tracker.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pollub.weather_mood_tracker.config.Language;
import pl.pollub.weather_mood_tracker.dto.*;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.Weather;
import pl.pollub.weather_mood_tracker.service.QuoteService;
import pl.pollub.weather_mood_tracker.service.UserService;
import pl.pollub.weather_mood_tracker.service.WeatherService;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final WeatherService weatherService;
    private final QuoteService quoteService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registrationDto, locale);
            Language language = new Language(locale);
            redirectAttributes.addFlashAttribute("success",
                    language.getMessage("user.registration.success"));
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new UserLoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDto") UserLoginDto loginDto,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Locale locale) {

        if (result.hasErrors()) {
            return "login";
        }

        Optional<User> userOpt = userService.loginUser(loginDto, locale);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            if (user.getSettings() != null) {
                session.setAttribute("userTheme", user.getSettings().getTheme().name());
                session.setAttribute("userLanguage", user.getSettings().getPreferredLanguage().name());
            }

            return "redirect:/dashboard";
        } else {
            Language language = new Language(locale);
            redirectAttributes.addFlashAttribute("error", language.getMessage("user.login.invalid"));
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes, Locale locale) {
        session.invalidate();
        Language language = new Language(locale);
        redirectAttributes.addFlashAttribute("success",
                language.getMessage("user.logout.success"));
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        if (!model.containsAttribute("moodEntry")) model.addAttribute("moodEntry", new MoodEntryDto());
        if (!model.containsAttribute("hydrationEntry")) model.addAttribute("hydrationEntry", new HydrationEntryDto());
        if (!model.containsAttribute("activityEntry")) model.addAttribute("activityEntry", new ActivityEntryDto());

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            String langCode = "pl";
            String theme = "light";
            if (user.getSettings() != null) {
                theme = user.getSettings().getTheme().name().toLowerCase();
                langCode = user.getSettings().getPreferredLanguage().name().toLowerCase();
            }
            model.addAttribute("userTheme", theme);
            model.addAttribute("userLanguage", langCode);

            String quote = quoteService.getQuoteOfDay(langCode);
            model.addAttribute("dailyQuote", quote);

            Weather currentWeather = weatherService.getOrCreateWeather(user.getCity());
            model.addAttribute("currentWeather", currentWeather);

            String advice = getWeatherAdvice(currentWeather.getWeatherType().name(), langCode);
            model.addAttribute("weatherAdvice", advice);

            String city = user.getCity();
            long usersInCity = userService.countUsersInCity(city);

            Double vibeToday = userService.getCityAverageMoodForDate(city, LocalDate.now());
            Double vibeYesterday = userService.getCityAverageMoodForDate(city, LocalDate.now().minusDays(1));

            model.addAttribute("usersInCity", usersInCity);
            model.addAttribute("vibeToday", vibeToday);
            model.addAttribute("vibeYesterday", vibeYesterday);

            return "dashboard";
        }

        return "redirect:/login";
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

    @PostMapping("/api/settings/city")
    @ResponseBody
    public ResponseEntity<?> updateCity(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();

        String newCity = request.get("city");
        boolean updated = userService.updateUserCity(userId, newCity);

        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid city name"));
        }
    }

    @PostMapping("/api/settings/theme")
    @ResponseBody
    public ResponseEntity<?> updateTheme(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String theme = request.get("theme");
        userService.updateUserTheme(userId, theme);

        session.setAttribute("userTheme", theme.toUpperCase());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}