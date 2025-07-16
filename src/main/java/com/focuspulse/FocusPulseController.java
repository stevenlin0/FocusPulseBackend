package com.focuspulse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FocusPulseController {

    private final JdbcTemplate jdbcTemplate;

    // Basic health check
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to FocusPulseAPI!");
    }

    // SETTINGS ENDPOINTS
    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        return jdbcTemplate.queryForMap("SELECT focus_minutes, break_minutes FROM settings LIMIT 1");
    }

    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody Map<String, Integer> settings) {
        jdbcTemplate.update(
                "UPDATE settings SET focus_minutes = ?, break_minutes = ? WHERE id = 1",
                settings.get("focus_minutes"),
                settings.get("break_minutes")
        );
        return ResponseEntity.ok("Settings Updated!");
    }

    // SESSIONS ENDPOINTS
    @GetMapping("/sessions")
    public List<Map<String, Object>> getSessions() {
        return jdbcTemplate.queryForList("SELECT * FROM sessions ORDER BY id DESC");
    }

    @PostMapping("/sessions")
    public ResponseEntity<String> addSession(@RequestBody Map<String, Object> session) {
        jdbcTemplate.update(
                "INSERT INTO sessions (session_date, duration, type, start_time, end_time) VALUES (?, ?, ?, ?, ?)",
                session.get("session_date"),
                session.get("duration"),
                session.get("type"),
                session.get("start_time"),
                session.get("end_time")
        );
        return ResponseEntity.ok("Session Saved!");
    }
}
