package com.focuspulse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling FocusPulse-related endpoints.
 * Provides basic CRUD operations for settings and focus sessions.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FocusPulseController {

    // Injects JdbcTemplate to interact with the database
    private final JdbcTemplate jdbcTemplate;

    /**
     * Basic welcome endpoint to verify the API is up.
     * @return Welcome message
     */
    @PostMapping("/")
    public ResponseEntity<String> welcome(){
        return ResponseEntity.ok("Welcome to FocusPulseAPI!");
    }

    /**
     * Retrieves focus and break settings from the database.
     * @return A map containing "focus_minutes" and "break_minutes"
     */
    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        return jdbcTemplate.queryForMap("SELECT focus_minutes, break_minutes FROM settings LIMIT 1");
    }

    /**
     * Updates focus and break settings in the database.
     * Expects a JSON body with keys: "focus_minutes" and "break_minutes".
     * @param settings A map with updated values
     * @return Confirmation message
     */
    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody Map<String, Integer> settings) {
        jdbcTemplate.update("UPDATE settings SET focus_minutes = ?, break_minutes = ? WHERE id = 1",
                settings.get("focus_minutes"), settings.get("break_minutes"));
        return ResponseEntity.ok("Settings Updated!");
    }

    /**
     * Retrieves a list of all focus sessions from the database.
     * Sessions are ordered by ID in descending order (latest first).
     * @return A list of session records
     */
    @GetMapping("/sessions")
    public List<Map<String, Object>> getSessions() {
        return jdbcTemplate.queryForList("SELECT * FROM sessions ORDER BY id DESC");
    }

    /**
     * Adds a new focus session to the database.
     * Expects a JSON body with keys: "session_date" and "focus_minutes".
     * @param session A map representing the new session
     * @return Confirmation message
     */
    @PostMapping("/sessions")
    public ResponseEntity<String> addSession(@RequestBody Map<String, Object> session) {
        jdbcTemplate.update("INSERT INTO sessions (session_date, focus_minutes) VALUES (?, ?)",
                session.get("session_date"), session.get("focus_minutes"));
        return ResponseEntity.ok("Session Saved!");
    }
}
