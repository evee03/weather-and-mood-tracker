package pl.pollub.weather_mood_tracker.model.enums;

public enum Language {
    PL("pl", "PL"),
    EN("en", "US");

    private final String language;
    private final String country;

    Language(String language, String country) {
        this.language = language;
        this.country = country;
    }

    public java.util.Locale toLocale() {
        return new java.util.Locale(language, country);
    }

    public static Language fromString(String lang) {
        if (lang == null) return PL;
        return switch (lang.toLowerCase()) {
            case "en" -> EN;
            case "pl" -> PL;
            default -> PL;
        };
    }
}
