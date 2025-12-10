package pl.pollub.weather_mood_tracker.config;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class MoodMessagesTest {

    @Test
    void shouldGetMoodFormHeadingPL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("mood.form.heading", localePL);

        assertEquals("Jak się dziś czujesz?", message);
    }

    @Test
    void shouldGetMoodFormHeadingEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("mood.form.heading", localeEN);

        assertEquals("How are you feeling today?", message);
    }

    @Test
    void shouldGetMoodFormCommentPlaceholderPL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("mood.form.comment.placeholder", localePL);

        assertEquals("Co się dzisiaj wydarzyło...", message);
    }

    @Test
    void shouldGetMoodFormCommentPlaceholderEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("mood.form.comment.placeholder", localeEN);

        assertEquals("What happened today...", message);
    }

    @Test
    void shouldGetFutureModalMessagePL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("modal.future", localePL);

        assertEquals("Ten dzień jeszcze nie nadszedł. Wróć  później, aby zapisać  nastrój!", message);
    }

    @Test
    void shouldGetFutureModalMessageEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("modal.future", localeEN);

        assertEquals("This day hasn't arrived yet. Come back later to save the mood!", message);
    }

    @Test
    void shouldGetHydrationMoodStatsTitlePL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("stats.charts.hydration.mood", localePL);

        assertEquals("Nawodnienie a Nastrój", message);
    }

    @Test
    void shouldGetHydrationMoodStatsTitleEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("stats.charts.hydration.mood", localeEN);

        assertEquals("Hydration vs Mood", message);
    }

    @Test
    void shouldGetPressureMoodStatsTitlePL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("stats.charts.pressure.mood", localePL);

        assertEquals("Ciśnienie a Nastrój", message);
    }

    @Test
    void shouldGetPressureMoodStatsTitleEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("stats.charts.pressure.mood", localeEN);

        assertEquals("Pressure vs Mood", message);
    }

    @Test
    void shouldGetHumidityMoodStatsTitlePL() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");

        String message = messageProvider.getMessage("stats.charts.humidity.mood", localePL);

        assertEquals("Wilgotność a Nastrój", message);
    }

    @Test
    void shouldGetHumidityMoodStatsTitleEN() {
        MessageProvider messageProvider = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String message = messageProvider.getMessage("stats.charts.humidity.mood", localeEN);

        assertEquals("Humidity vs Mood", message);
    }

    @Test
    void shouldGetPersonalVsCommunityStatsTitlePLorEN() {
        MessageProvider messageProviderPL = new MessageProvider();
        Locale localePL = Locale.of("pl", "PL");
        MessageProvider messageProviderEN = new MessageProvider();
        Locale localeEN = Locale.of("en", "US");

        String messagePL = messageProviderPL.getMessage("stats.charts.personal.vs.community", localePL);
        assertEquals("Twój Nastrój vs Reszta Miasta (30 dni)", messagePL);
        String messageEN = messageProviderEN.getMessage("stats.charts.personal.vs.community", localeEN);
        assertEquals("Your Mood vs City Average (30 days)", messageEN);
    }

}
