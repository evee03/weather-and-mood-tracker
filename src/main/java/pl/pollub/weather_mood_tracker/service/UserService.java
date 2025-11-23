package pl.pollub.weather_mood_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.config.Language;
import pl.pollub.weather_mood_tracker.dto.UserLoginDto;
import pl.pollub.weather_mood_tracker.dto.UserRegistrationDto;
import pl.pollub.weather_mood_tracker.model.Settings;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.enums.Theme;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WeatherService weatherService;
    private final MoodRepository moodRepository;

    // W UserService.java

    @Transactional
    public User registerUser(UserRegistrationDto dto, Locale locale) throws Exception {
        Language language = new Language(locale);

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception(language.getMessage("user.passwords.not.match"));
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new Exception(language.getMessage("user.username.exists"));
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception(language.getMessage("user.email.exists"));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        String city = (dto.getCity() != null && !dto.getCity().trim().isEmpty()) ? dto.getCity() : "Warszawa";
        if (weatherService.isCityValid(city)) {
            user.setCity(city);
        } else {
            user.setCity("Warszawa");
        }

        user.setSettings(Settings.builder()
                .user(user)
                .preferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.PL)
                .theme(Theme.LIGHT)
                .build());

        return userRepository.save(user);
    }

    public Optional<User> loginUser(UserLoginDto loginDto, Locale locale) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail());
        if (userOpt.isPresent() && passwordEncoder.matches(loginDto.getPassword(), userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    @Transactional
    public void updateUserLanguage(Long userId, Locale locale) {
        findById(userId).ifPresent(user -> {
            if(user.getSettings() == null) user.setSettings(Settings.builder().user(user).theme(Theme.LIGHT).build());
            user.getSettings().setPreferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.fromString(locale.getLanguage()));
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUserTheme(Long userId, String themeStr) {
        findById(userId).ifPresent(user -> {
            if(user.getSettings() == null) user.setSettings(Settings.builder().user(user).preferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.PL).build());
            user.getSettings().setTheme(themeStr.equalsIgnoreCase("DARK") ? Theme.DARK : Theme.LIGHT);
            userRepository.save(user);
        });
    }

    @Transactional
    public boolean updateUserCity(Long userId, String newCity) {
        Optional<User> u = findById(userId);
        if (u.isPresent() && weatherService.isCityValid(newCity)) {
            u.get().setCity(newCity);
            userRepository.save(u.get());
            return true;
        }
        return false;
    }

    public long countUsersInCity(String city) {
        if (city == null || city.trim().isEmpty()) return 0;
        return userRepository.countByCity(city);
    }

    public Double getCityAverageMoodForDate(String city, LocalDate date) {
        if (city == null || city.trim().isEmpty()) return 0.0;
        Double avg = moodRepository.findAverageMoodByCityAndDate(city, date);
        return avg != null ? avg : 0.0;
    }
}