package pl.pollub.weather_mood_tracker.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@GroupSequence({UserLoginDto.class, NotEmpty.class})
public class UserLoginDto {

    @NotBlank(message = "{user.username.required}", groups = NotEmpty.class)
    private String usernameOrEmail;

    @NotBlank(message = "{user.password.required}", groups = NotEmpty.class)
    private String password;
}