package pl.pollub.weather_mood_tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.weather_mood_tracker.model.Weather;
import pl.pollub.weather_mood_tracker.model.enums.WeatherType;
import pl.pollub.weather_mood_tracker.repository.WeatherRepository;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final WebClient webClient;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Transactional
    public Weather getOrCreateWeather(String city) {
        LocalDate today = LocalDate.now();

        String targetCity = (city == null || city.trim().isEmpty()) ? "Warszawa" : city;

        Optional<Weather> existingWeather = weatherRepository.findByCityAndDate(targetCity, today);
        if (existingWeather.isPresent()) {
            return existingWeather.get();
        }

        Weather newWeather = fetchWeatherFromApi(targetCity);

        return weatherRepository.save(newWeather);
    }

    private Weather fetchWeatherFromApi(String city) {
        try {
            JsonNode response = webClient.get()
                    .uri(apiUrl, uriBuilder -> uriBuilder
                            .queryParam("q", city)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("No response from OpenWeatherMap API");
            }

            String cityName = response.path("name").asText();
            double temp = response.path("main").path("temp").asDouble();
            int humidity = response.path("main").path("humidity").asInt();
            int pressure = response.path("main").path("pressure").asInt();
            String weatherMain = response.path("weather").get(0).path("main").asText();

            return Weather.builder()
                    .city(cityName)
                    .date(LocalDate.now())
                    .temperature(temp)
                    .humidity(humidity)
                    .pressure(pressure)
                    .weatherType(mapWeatherType(weatherMain))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Weather.builder()
                    .city(city)
                    .date(LocalDate.now())
                    .temperature(10.0)
                    .humidity(50)
                    .pressure(1013)
                    .weatherType(WeatherType.CLOUDY)
                    .build();
        }
    }

    private WeatherType mapWeatherType(String apiWeather) {
        return switch (apiWeather.toLowerCase()) {
            case "rain" -> WeatherType.RAINY;
            case "snow" -> WeatherType.SNOWY;
            case "clear" -> WeatherType.SUNNY;
            case "storm" -> WeatherType.STORMY;
            case "wind" -> WeatherType.WINDY;
            case "fog" -> WeatherType.FOGGY;
            default -> WeatherType.CLOUDY;
        };
    }
}