package pl.pollub.weather_mood_tracker.config;

import java.util.Locale;
import java.util.ResourceBundle;

public class Language {
    private Locale locale;
    private ResourceBundle resourceBundle;

    public Language(String language, String country) {
        this.locale = new Locale(language, country);
        this.resourceBundle = ResourceBundle.getBundle("messages", locale);
    }

    public Language(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("messages", locale);
    }

    public String getMessage(String key) {
        return resourceBundle.getString(key);
    }

    public String getMessage(String key, Object... args) {
        String message = resourceBundle.getString(key);
        return java.text.MessageFormat.format(message, args);
    }
}