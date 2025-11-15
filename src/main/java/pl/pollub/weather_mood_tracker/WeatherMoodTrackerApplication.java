package pl.pollub.weather_mood_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class WeatherMoodTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherMoodTrackerApplication.class, args);
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().build();
	}
}
