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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

import java.util.Locale;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class TrackerController {

    private final HydrationService hydrationService;
    private final PhysicalActivityService activityService;

    @PostMapping("/hydration")
    @ResponseBody
    public ResponseEntity<?> addHydration(@Valid @ModelAttribute("hydrationEntry") HydrationEntryDto dto,
                                          BindingResult result,
                                          HttpSession session,
                                          Locale locale) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();

        Language lang = new Language(locale);

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", lang.getMessage("tracker.error")));
        }

        try {
            hydrationService.addHydration(userId, dto);
            return ResponseEntity.ok(Map.of("message", lang.getMessage("tracker.hydration.saved")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/activity")
    @ResponseBody
    public ResponseEntity<?> addActivity(@Valid @ModelAttribute("activityEntry") ActivityEntryDto dto,
                                         BindingResult result,
                                         HttpSession session,
                                         Locale locale) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();

        Language lang = new Language(locale);

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", lang.getMessage("tracker.error")));
        }

        try {
            activityService.addActivity(userId, dto);
            return ResponseEntity.ok(Map.of("message", lang.getMessage("tracker.activity.saved")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}