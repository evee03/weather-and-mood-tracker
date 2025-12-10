package pl.pollub.weather_mood_tracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.auth.UserService;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserLocaleResolver implements LocaleResolver {

    private final UserService userService;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isPresent() && userOpt.get().getSettings() != null) {
                return userOpt.get().getSettings().getPreferredLanguage().toLocale();
            }
        }

        if (session != null && session.getAttribute("locale") != null) {
            return (Locale) session.getAttribute("locale");
        }

        return new Locale("pl", "PL");
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        HttpSession session = request.getSession(true);
        session.setAttribute("locale", locale);

        if (session.getAttribute("userId") != null) {
            Long userId = (Long) session.getAttribute("userId");
            userService.updateUserLanguage(userId, locale);
        }
    }
}