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
import pl.pollub.weather_mood_tracker.dto.UserLoginDto;
import pl.pollub.weather_mood_tracker.dto.UserRegistrationDto;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.service.UserService;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            // Dodaj preferencje do modelu
            if (user.getSettings() != null) {
                model.addAttribute("userTheme", user.getSettings().getTheme().name().toLowerCase());
                model.addAttribute("userLanguage", user.getSettings().getPreferredLanguage().name().toLowerCase());
            } else {
                model.addAttribute("userTheme", "light");
                model.addAttribute("userLanguage", "pl");
            }

            return "dashboard";
        }

        return "redirect:/login";
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