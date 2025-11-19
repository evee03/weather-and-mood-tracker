package pl.pollub.weather_mood_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        model.addAttribute("today", LocalDate.now().toString());
        return "calendar";
    }
}
