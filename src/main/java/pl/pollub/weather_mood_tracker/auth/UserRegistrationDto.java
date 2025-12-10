package pl.pollub.weather_mood_tracker.auth;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@GroupSequence({UserRegistrationDto.class, NotEmpty.class, Size.class, Pattern.class})
public class UserRegistrationDto {

    @NotBlank(message = "{user.username.required}", groups = NotEmpty.class)
    @Size(min = 3, max = 50, message = "{user.username.size}", groups = Size.class)
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "{user.username.pattern}",
            groups = Pattern.class
    )
    private String username;

    @NotBlank(message = "{user.email.required}", groups = NotEmpty.class)
    @Email(message = "{user.email.invalid}", groups = Pattern.class)
    private String email;

    @NotBlank(message = "{user.password.required}", groups = NotEmpty.class)
    @Size(min = 8, max = 100, message = "{user.password.size}", groups = Size.class)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!*()_\\-]).*$",
            message = "{user.password.pattern}",
            groups = Pattern.class
    )
    private String password;

    @NotBlank(message = "{user.confirmPassword.required}", groups = NotEmpty.class)
    private String confirmPassword;

    private String city;
}