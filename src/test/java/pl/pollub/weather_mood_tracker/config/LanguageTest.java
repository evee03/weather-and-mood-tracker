package pl.pollub.weather_mood_tracker.config;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void shouldGetPolishMessage() {
        Language language = new Language(new Locale("pl", "PL"));

        String message = language.getMessage("user.username.required");

        assertEquals("Nazwa użytkownika jest wymagana", message);
    }

    @Test
    void shouldGetEnglishMessage() {
        Language language = new Language(new Locale("en", "US"));

        String message = language.getMessage("user.username.required");

        assertEquals("Username is required", message);
    }

    @Test
    void shouldFormatMessageWithParameters() {
        Language language = new Language(new Locale("pl", "PL"));

        String message = language.getMessage("user.username.size", 3, 50);

        assertEquals("Nazwa użytkownika musi mieć od 3 do 50 znaków", message);
    }

    @Test
    void shouldGetPageTitle() {
        Language languagePL = new Language(new Locale("pl", "PL"));
        Language languageEN = new Language(new Locale("en", "US"));

        assertEquals("Logowanie - Weather Mood Tracker",
                languagePL.getMessage("page.login.title"));
        assertEquals("Login - Weather Mood Tracker",
                languageEN.getMessage("page.login.title"));
    }

    @Test
    void shouldGetDashboardWelcomeMessage() {
        Language language = new Language(new Locale("pl", "PL"));

        String message = language.getMessage("page.dashboard.welcome", "Jan");

        assertEquals("Witaj, Jan!", message);
    }
}
