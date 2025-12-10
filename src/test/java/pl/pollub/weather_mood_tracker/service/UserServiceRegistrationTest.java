package pl.pollub.weather_mood_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pollub.weather_mood_tracker.auth.UserRegistrationDto;
import pl.pollub.weather_mood_tracker.auth.UserService;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.MoodRepository;
import pl.pollub.weather_mood_tracker.repository.UserRepository;
import pl.pollub.weather_mood_tracker.testutil.TestDataFactory;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WeatherService weatherService;

    @Mock
    private MoodRepository moodRepository;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto validRegistrationDto;
    private User validUser;

    @BeforeEach
    void setUp() {
        validRegistrationDto = TestDataFactory.registrationDto("WalterWhite", "WalterWhite@meth.com", "BlueShy123!", "Lublin");
        validUser = TestDataFactory.user(1L, "WalterWhite", "WalterWhite@meth.com", "encodedPassword", "Lublin");
    }

    @Test
    void registerUser_Success() throws Exception {
        when(weatherService.isCityValid(anyString())).thenReturn(true);

        when(userRepository.existsByUsername("WalterWhite")).thenReturn(false);
        when(userRepository.existsByEmail("WalterWhite@meth.com")).thenReturn(false);
        when(passwordEncoder.encode("BlueShy123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        User result = userService.registerUser(validRegistrationDto, new Locale("pl", "PL"));

        assertNotNull(result);
        assertEquals("WalterWhite", result.getUsername());
        assertEquals("WalterWhite@meth.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("BlueShy123!");
    }

    @Test
    void registerUser_PasswordsDoNotMatch_ThrowsException() {
        validRegistrationDto.setConfirmPassword("differentPassword");

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(validRegistrationDto, new Locale("en", "US"))
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ConfirmPasswordMissing_ThrowsException() {
        validRegistrationDto.setConfirmPassword(null);

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(validRegistrationDto, new Locale("en", "US"))
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername("WalterWhite")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(validRegistrationDto, new Locale("en", "US"))
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername("WalterWhite")).thenReturn(false);
        when(userRepository.existsByEmail("WalterWhite@meth.com")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(validRegistrationDto, new Locale("en", "US"))
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithoutCity_Success() throws Exception {
        validRegistrationDto.setCity(null);

        when(userRepository.existsByUsername("WalterWhite")).thenReturn(false);
        when(userRepository.existsByEmail("WalterWhite@meth.com")).thenReturn(false);
        when(passwordEncoder.encode("BlueShy123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        User result = userService.registerUser(validRegistrationDto, new Locale("pl", "PL"));

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_CityEmpty_Success() throws Exception {
        validRegistrationDto.setCity("");

        when(userRepository.existsByUsername("WalterWhite")).thenReturn(false);
        when(userRepository.existsByEmail("WalterWhite@meth.com")).thenReturn(false);
        when(passwordEncoder.encode("BlueShy123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.registerUser(validRegistrationDto, new Locale("pl", "PL"));

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}