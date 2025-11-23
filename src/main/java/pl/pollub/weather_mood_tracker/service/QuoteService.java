package pl.pollub.weather_mood_tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pollub.weather_mood_tracker.model.enums.Language;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final WebClient webClient;

    public String getQuoteOfDay(String languageCode) {
        String apiUrl;
        String defaultQuote;

        if ("pl".equalsIgnoreCase(languageCode)) {
            apiUrl = "https://dailystoic.pl/quote/text_pl.json";
            defaultQuote = "Nie to co nam się przydarza, ale to jak na to reagujemy, ma znaczenie.";
        } else {
            apiUrl = "https://dailystoic.pl/quote/text_en.json";
            defaultQuote = "It is not what happens to you, but how you react to it that matters.";
        }

        try {
            JsonNode response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("quote")) {
                return response.get("quote").asText();
            } else if (response != null && response.has("text")) {
                return response.get("text").asText();
            }
            return defaultQuote;

        } catch (Exception e) {
            return defaultQuote;
        }
    }
}