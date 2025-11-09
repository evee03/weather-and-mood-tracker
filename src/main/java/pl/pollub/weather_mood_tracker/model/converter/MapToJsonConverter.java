package pl.pollub.weather_mood_tracker.model.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, Double>, String> {
    private final ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Double> attribute) {
        if (attribute == null) return null;
        try{
            return objectMapper.writeValueAsString(attribute);
        } catch(Exception e){
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try{
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch(Exception e){
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}
