package pl.pollub.weather_mood_tracker.config;

import org.springframework.stereotype.Component;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MessageProvider {

    public String getMessage(String key, Locale locale) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public String getMessage(String key, Locale locale, Object... args) {
        String message = getMessage(key, locale);
        return java.text.MessageFormat.format(message, args);
    }
}