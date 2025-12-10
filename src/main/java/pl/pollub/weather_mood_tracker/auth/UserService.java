package pl.pollub.weather_mood_tracker.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// 1. Importujemy Twoją nową klasę do tłumaczeń (zmieniona nazwa z Language)
import pl.pollub.weather_mood_tracker.config.MessageProvider;
import pl.pollub.weather_mood_tracker.model.Settings;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.model.enums.Theme;
// 2. Teraz możemy bezpiecznie zaimportować enum Language bez konfliktu
import pl.pollub.weather_mood_tracker.model.enums.Language;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;
// 3. Import stałych (utwórz ten plik zgodnie z zaleceniem)
import pl.pollub.weather_mood_tracker.service.WeatherService;
import pl.pollub.weather_mood_tracker.util.AppConstants;

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
    private final MessageProvider messageProvider;

    @Transactional
    public User registerUser(UserRegistrationDto dto, Locale locale) throws Exception {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception(messageProvider.getMessage("user.passwords.not.match", locale));
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new Exception(messageProvider.getMessage("user.username.exists", locale));
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception(messageProvider.getMessage("user.email.exists", locale));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        String city = (dto.getCity() != null && !dto.getCity().trim().isEmpty())
                ? dto.getCity()
                : AppConstants.DEFAULT_CITY;

        if (weatherService.isCityValid(city)) {
            user.setCity(city);
        } else {
            user.setCity(AppConstants.DEFAULT_CITY);
        }

        user.setSettings(Settings.builder()
                .user(user)
                .preferredLanguage(Language.PL)
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
            if(user.getSettings() == null) {
                user.setSettings(Settings.builder().user(user).theme(Theme.LIGHT).build());
            }
            user.getSettings().setPreferredLanguage(Language.fromString(locale.getLanguage()));
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUserTheme(Long userId, String themeStr) {
        findById(userId).ifPresent(user -> {
            if(user.getSettings() == null) {
                user.setSettings(Settings.builder().user(user).preferredLanguage(Language.PL).build());
            }
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