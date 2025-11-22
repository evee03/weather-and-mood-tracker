package pl.pollub.weather_mood_tracker.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pollub.weather_mood_tracker.config.Language;
import pl.pollub.weather_mood_tracker.dto.MoodEntryDto;
import pl.pollub.weather_mood_tracker.service.MoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @PostMapping("/dashboard/mood")
    @ResponseBody
    public ResponseEntity<?> saveMood(@Valid @ModelAttribute("moodEntry") MoodEntryDto moodEntryDto,
                                      BindingResult bindingResult,
                                      HttpSession session,
                                      Locale locale) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Language language = new Language(locale);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", language.getMessage("tracker.error")));
        }

        try {
            moodService.saveMood(moodEntryDto, userId, locale);
            return ResponseEntity.ok(Map.of("message", language.getMessage("mood.save.success")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}