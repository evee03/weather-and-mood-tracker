package pl.pollub.weather_mood_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pollub.weather_mood_tracker.model.User;
import pl.pollub.weather_mood_tracker.repository.UserRepository;
import pl.pollub.weather_mood_tracker.testutil.TestDataFactory;
import pl.pollub.weather_mood_tracker.dto.UserLoginDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = TestDataFactory.user(1L, "WalterWhite", "WalterWhite@example.com", "encodedPassword", "Lublin");
    }

    @Test
    void loginUser_WithUsername_Success() {
        UserLoginDto login = TestDataFactory.loginDto("WalterWhite", "bluesky123!");
        when(userRepository.findByUsernameOrEmail("WalterWhite", "WalterWhite")).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches("bluesky123!", "encodedPassword")).thenReturn(true);

        Optional<User> result = userService.loginUser(login);

        assertTrue(result.isPresent());
        assertEquals("WalterWhite", result.get().getUsername());
        verify(passwordEncoder, times(1)).matches("bluesky123!", "encodedPassword");
    }

    @Test
    void loginUser_WrongPassword_ReturnsEmpty() {
        UserLoginDto login = TestDataFactory.loginDto("WalterWhite", "crappymeth");
        when(userRepository.findByUsernameOrEmail("WalterWhite", "WalterWhite")).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches("crappymeth", "encodedPassword")).thenReturn(false);

        Optional<User> result = userService.loginUser(login);

        assertFalse(result.isPresent());
    }

    @Test
    void loginUser_UserNotFound_ReturnsEmpty() {
        UserLoginDto login = TestDataFactory.loginDto("ghost_user", "blank");
        when(userRepository.findByUsernameOrEmail("ghost_user", "ghost_user")).thenReturn(Optional.empty());

        Optional<User> result = userService.loginUser(login);

        assertFalse(result.isPresent());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

}