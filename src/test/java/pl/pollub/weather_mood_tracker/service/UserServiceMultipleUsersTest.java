package pl.pollub.weather_mood_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pollub.weather_mood_tracker.dto.UserRegistrationDto;
import pl.pollub.weather_mood_tracker.dto.UserLoginDto;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.UserRepository;
import pl.pollub.weather_mood_tracker.testutil.TestDataFactory;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMultipleUsersTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private UserService userService;

    private User aniaUser;
    private User janUser;

    private UserRegistrationDto aniaDto;
    private UserRegistrationDto janDto;

    @BeforeEach
    void setUp() {
        aniaDto = TestDataFactory.registrationDto("ania.kowalska", "ania.kowalska@example.com", "HasloMaslo123!", "Kraków");
        janDto = TestDataFactory.registrationDto("JanNowak", "jan.nowak@example.com", "SuperTajne@Haslo9!", "Warszawa");

        aniaUser = TestDataFactory.user(2L, "ania.kowalska", "ania.kowalska@example.com", "encodedAniaPassword", "Kraków");
        janUser = TestDataFactory.user(3L, "JanNowak", "jan.nowak@example.com", "encodedJanPassword", "Warszawa");
    }

    @Test
    void registerMultipleUsers_Success() throws Exception {
        when(weatherService.isCityValid(anyString())).thenReturn(true);

        when(userRepository.existsByUsername("ania.kowalska")).thenReturn(false);
        when(userRepository.existsByEmail("ania.kowalska@example.com")).thenReturn(false);
        when(passwordEncoder.encode("HasloMaslo123!")).thenReturn("encodedAniaPassword");
        when(userRepository.save(any(User.class))).thenReturn(aniaUser);

        User aniaResult = userService.registerUser(aniaDto, new Locale("pl", "PL"));

        assertNotNull(aniaResult);
        assertEquals("ania.kowalska", aniaResult.getUsername());
        assertEquals("Kraków", aniaResult.getCity());

        when(userRepository.existsByUsername("JanNowak")).thenReturn(false);
        when(userRepository.existsByEmail("jan.nowak@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SuperTajne@Haslo9!")).thenReturn("encodedJanPassword");
        when(userRepository.save(any(User.class))).thenReturn(janUser);

        User janResult = userService.registerUser(janDto, new Locale("pl", "PL"));

        assertNotNull(janResult);
        assertEquals("JanNowak", janResult.getUsername());
        assertEquals("Warszawa", janResult.getCity());

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void loginMultipleUsers_BothSuccess() {
        UserLoginDto aniaLogin = TestDataFactory.loginDto("ania.kowalska", "HasloMaslo123!");
        when(userRepository.findByUsernameOrEmail("ania.kowalska", "ania.kowalska")).thenReturn(Optional.of(aniaUser));
        when(passwordEncoder.matches("HasloMaslo123!", "encodedAniaPassword")).thenReturn(true);

        Optional<User> aniaResult = userService.loginUser(aniaLogin, new Locale("pl", "PL"));
        assertTrue(aniaResult.isPresent());
        assertEquals("ania.kowalska", aniaResult.get().getUsername());
        assertEquals("Kraków", aniaResult.get().getCity());

        UserLoginDto janLogin = TestDataFactory.loginDto("jan.nowak@example.com", "SuperTajne@Haslo9!");
        when(userRepository.findByUsernameOrEmail("jan.nowak@example.com", "jan.nowak@example.com")).thenReturn(Optional.of(janUser));
        when(passwordEncoder.matches("SuperTajne@Haslo9!", "encodedJanPassword")).thenReturn(true);

        Optional<User> janResult = userService.loginUser(janLogin, new Locale("pl", "PL"));
        assertTrue(janResult.isPresent());
        assertEquals("JanNowak", janResult.get().getUsername());
        assertEquals("Warszawa", janResult.get().getCity());
    }

    @Test
    void registerUser_UsernameConflict_WhenAniaAlreadyExists() {
        when(userRepository.existsByUsername("ania.kowalska")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(aniaDto, new Locale("en", "US"))
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailConflict_WhenJanEmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("jan.nowak@example.com")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () ->
                userService.registerUser(janDto, new Locale("en", "US"))
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_OneSuccessOneFailure() {
        UserLoginDto aniaLogin = TestDataFactory.loginDto("ania.kowalska", "HasloMaslo123!");
        when(userRepository.findByUsernameOrEmail("ania.kowalska", "ania.kowalska")).thenReturn(Optional.of(aniaUser));
        when(passwordEncoder.matches("HasloMaslo123!", "encodedAniaPassword")).thenReturn(true);

        Optional<User> aniaResult = userService.loginUser(aniaLogin, new Locale("pl", "PL"));
        assertTrue(aniaResult.isPresent());

        UserLoginDto janLogin = TestDataFactory.loginDto("JanNowak", "ZleHaslo123");
        when(userRepository.findByUsernameOrEmail("JanNowak", "JanNowak")).thenReturn(Optional.of(janUser));
        when(passwordEncoder.matches("ZleHaslo123", "encodedJanPassword")).thenReturn(false);

        Optional<User> janResult = userService.loginUser(janLogin, new Locale("pl", "PL"));
        assertFalse(janResult.isPresent());
    }

    @Test
    void findById_MultipleUsers_ReturnsCorrectUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(aniaUser));
        when(userRepository.findById(3L)).thenReturn(Optional.of(janUser));

        Optional<User> aniaResult = userService.findById(2L);
        Optional<User> janResult = userService.findById(3L);

        assertTrue(aniaResult.isPresent());
        assertEquals("ania.kowalska", aniaResult.get().getUsername());
        assertEquals("Kraków", aniaResult.get().getCity());

        assertTrue(janResult.isPresent());
        assertEquals("JanNowak", janResult.get().getUsername());
        assertEquals("Warszawa", janResult.get().getCity());
    }

    @Test
    void registerUsers_DifferentCities_Success() throws Exception {
        UserRegistrationDto poznanUser = TestDataFactory.registrationDto("user.poznan", "poznan@example.com", "Pass123!", "Poznań");
        UserRegistrationDto wroclawUser = TestDataFactory.registrationDto("user.wroclaw", "wroclaw@example.com", "Pass456!", "Wrocław");

        when(weatherService.isCityValid(anyString())).thenReturn(true);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        User poznanResult = userService.registerUser(poznanUser, new Locale("pl", "PL"));
        User wroclawResult = userService.registerUser(wroclawUser, new Locale("pl", "PL"));

        assertNotNull(poznanResult);
        assertEquals("Poznań", poznanResult.getCity());

        assertNotNull(wroclawResult);
        assertEquals("Wrocław", wroclawResult.getCity());

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void loginUser_Nonexistent_ReturnsEmpty() {
        UserLoginDto login = TestDataFactory.loginDto("noone", "somePassword");
        when(userRepository.findByUsernameOrEmail("noone", "noone")).thenReturn(Optional.empty());

        Optional<User> result = userService.loginUser(login, new Locale("pl", "PL"));

        assertFalse(result.isPresent());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}