package pl.pollub.weather_mood_tracker.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.pollub.weather_mood_tracker.dto.ActivityEntryDto;
import pl.pollub.weather_mood_tracker.dto.HydrationEntryDto;
import pl.pollub.weather_mood_tracker.dto.MoodEntryDto;
import pl.pollub.weather_mood_tracker.service.DashboardService;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("moodEntry", new MoodEntryDto());
        model.addAttribute("hydrationEntry", new HydrationEntryDto());
        model.addAttribute("activityEntry", new ActivityEntryDto());

        dashboardService.prepareDashboardData(model, userId);

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}