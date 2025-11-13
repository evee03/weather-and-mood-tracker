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
import pl.pollub.weather_mood_tracker.repository.UserRepository;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) throws Exception {
        return registerUser(registrationDto, new Locale("pl", "PL"));
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto, Locale locale) throws Exception {
        Language language = new Language(locale);

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new Exception(language.getMessage("user.passwords.not.match"));
        }

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new Exception(language.getMessage("user.username.exists"));
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new Exception(language.getMessage("user.email.exists"));
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setCity(registrationDto.getCity());

        Settings settings = Settings.builder()
                .user(user)
                .preferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.PL)
                .theme(Theme.LIGHT)
                .build();
        user.setSettings(settings);

        return userRepository.save(user);
    }

    public Optional<User> loginUser(UserLoginDto loginDto) {
        return loginUser(loginDto, new Locale("pl", "PL"));
    }

    public Optional<User> loginUser(UserLoginDto loginDto, Locale locale) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(
                loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail()
        );

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void updateUserLanguage(Long userId, Locale locale) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Settings settings = user.getSettings();

            if (settings == null) {
                settings = Settings.builder()
                        .user(user)
                        .preferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.fromString(locale.getLanguage()))
                        .theme(Theme.LIGHT)
                        .build();
                user.setSettings(settings);
            } else {
                settings.setPreferredLanguage(
                        pl.pollub.weather_mood_tracker.model.enums.Language.fromString(locale.getLanguage())
                );
            }

            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUserTheme(Long userId, String themeString) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Settings settings = user.getSettings();

            Theme theme = themeString.equalsIgnoreCase("DARK") ? Theme.DARK : Theme.LIGHT;

            if (settings == null) {
                settings = Settings.builder()
                        .user(user)
                        .preferredLanguage(pl.pollub.weather_mood_tracker.model.enums.Language.PL)
                        .theme(theme)
                        .build();
                user.setSettings(settings);
            } else {
                settings.setTheme(theme);
            }

            userRepository.save(user);
        }
    }
}