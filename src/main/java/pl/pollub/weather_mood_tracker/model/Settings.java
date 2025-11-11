package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.weather_mood_tracker.model.enums.Language;
import pl.pollub.weather_mood_tracker.model.enums.Theme;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 5)
    private Language preferredLanguage = Language.PL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Theme theme = Theme.LIGHT;
}