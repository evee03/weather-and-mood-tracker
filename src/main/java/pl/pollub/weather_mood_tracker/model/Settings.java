package pl.pollub.weather_mood_tracker.model;

import jakarta.persistence.*;
import lombok.*;
import pl.pollub.weather_mood_tracker.model.enums.Language;
import pl.pollub.weather_mood_tracker.model.enums.Theme;
import java.util.Objects;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 5)
    @Builder.Default
    private Language preferredLanguage = Language.PL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Theme theme = Theme.LIGHT;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Settings)) return false;
        Settings settings = (Settings) o;
        return id != null && Objects.equals(id, settings.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}