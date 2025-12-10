package pl.pollub.weather_mood_tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final WebClient webClient;

    @Value("${quote.api.url.pl}")
    private String apiUrlPl;

    @Value("${quote.api.url.en}")
    private String apiUrlEn;

    public String getQuoteOfDay(String languageCode) {
        String apiUrl = "pl".equalsIgnoreCase(languageCode) ? apiUrlPl : apiUrlEn;

        String defaultQuote;

        if ("pl".equalsIgnoreCase(languageCode)) {
            defaultQuote = "Nie to co nam się przydarza, ale to jak na to reagujemy, ma znaczenie.";
        } else {
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