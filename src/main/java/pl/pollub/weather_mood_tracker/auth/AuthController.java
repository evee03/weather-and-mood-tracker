package pl.pollub.weather_mood_tracker.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pollub.weather_mood_tracker.config.MessageProvider;
import pl.pollub.weather_mood_tracker.model.User;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final MessageProvider messageProvider;

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
            redirectAttributes.addFlashAttribute("success",
                    messageProvider.getMessage("user.registration.success", locale));
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
            redirectAttributes.addFlashAttribute("error", messageProvider.getMessage("user.login.invalid", locale));
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes, Locale locale) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success",
                messageProvider.getMessage("user.logout.success", locale));
        return "redirect:/login";
    }
}