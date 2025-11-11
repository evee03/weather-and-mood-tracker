package pl.pollub.weather_mood_tracker.testutil;

import pl.pollub.weather_mood_tracker.dto.UserLoginDto;
import pl.pollub.weather_mood_tracker.dto.UserRegistrationDto;
import pl.pollub.weather_mood_tracker.model.User;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static UserRegistrationDto registrationDto(String username, String email, String password, String city) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setConfirmPassword(password);
        dto.setCity(city);
        return dto;
    }

    public static UserLoginDto loginDto(String usernameOrEmail, String password) {
        return new UserLoginDto(usernameOrEmail, password);
    }

    public static User user(Long id, String username, String email, String encodedPassword, String city) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encodedPassword);
        u.setCity(city);
        return u;
    }
}