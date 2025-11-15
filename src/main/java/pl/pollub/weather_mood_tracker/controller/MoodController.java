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

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @PostMapping("/dashboard/mood")
    public String saveMood(@Valid @ModelAttribute("moodEntry") MoodEntryDto moodEntryDto,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           Locale locale) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("moodError", "Validation error.");
            return "redirect:/dashboard";
        }

        Language language = new Language(locale);
        try {
            moodService.saveMood(moodEntryDto, userId, locale);
            redirectAttributes.addFlashAttribute("moodSuccess",
                    language.getMessage("mood.save.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("moodError",
                    language.getMessage("mood.save.error", e.getMessage()));
        }

        return "redirect:/dashboard";
    }
}