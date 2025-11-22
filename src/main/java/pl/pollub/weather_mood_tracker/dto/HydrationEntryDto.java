package pl.pollub.weather_mood_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HydrationEntryDto {
    @NotNull
    @Min(value = 1, message = "{hydration.amount.min}")
    private Integer amountMl;
}