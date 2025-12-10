package pl.pollub.weather_mood_tracker.config;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void shouldGetPolishMessage() {
        MessageProvider messageProvider = new MessageProvider();
        Locale locale = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("user.username.required", locale);

        assertEquals("Nazwa użytkownika jest wymagana", message);
    }

    @Test
    void shouldGetEnglishMessage() {
        MessageProvider messageProvider = new MessageProvider();
        Locale locale = Locale.of("en", "US");

        String message = messageProvider.getMessage("user.username.required", locale);

        assertEquals("Username is required", message);
    }

    @Test
    void shouldGetPageTitle() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");
        Locale localeEN = Locale.of("en", "US");

        assertEquals("Logowanie - Weather Mood Tracker",
                messageProvider.getMessage("page.login.title", localePL));
        assertEquals("Login - Weather Mood Tracker",
                messageProvider.getMessage("page.login.title", localeEN));
    }

    @Test
    void shouldGetDashboardWelcomeMessage() {
        MessageProvider messageProvider = new MessageProvider();
        Locale locale = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("page.dashboard.welcome", locale, "Jan");

        assertEquals("Witaj, Jan!", message);
    }
}