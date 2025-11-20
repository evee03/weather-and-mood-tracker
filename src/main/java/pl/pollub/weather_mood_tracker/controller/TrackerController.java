package pl.pollub.weather_mood_tracker.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pollub.weather_mood_tracker.config.Language;
import pl.pollub.weather_mood_tracker.dto.ActivityEntryDto;
import pl.pollub.weather_mood_tracker.dto.HydrationEntryDto;
import pl.pollub.weather_mood_tracker.service.HydrationService;
import pl.pollub.weather_mood_tracker.service.PhysicalActivityService;

import java.util.Locale;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class TrackerController {

    private final HydrationService hydrationService;
    private final PhysicalActivityService activityService;

    @PostMapping("/hydration")
    public String addHydration(@Valid @ModelAttribute("hydrationEntry") HydrationEntryDto dto,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Language lang = new Language(locale);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("hydrationError", lang.getMessage("tracker.error"));
            return "redirect:/dashboard";
        }

        try {
            hydrationService.addHydration(userId, dto);
            redirectAttributes.addFlashAttribute("hydrationSuccess", lang.getMessage("tracker.hydration.saved"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("hydrationError", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/activity")
    public String addActivity(@Valid @ModelAttribute("activityEntry") ActivityEntryDto dto,
                              BindingResult result,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Locale locale) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Language lang = new Language(locale);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("activityError", lang.getMessage("tracker.error"));
            return "redirect:/dashboard";
        }

        try {
            activityService.addActivity(userId, dto);
            redirectAttributes.addFlashAttribute("activitySuccess", lang.getMessage("tracker.activity.saved"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("activityError", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}