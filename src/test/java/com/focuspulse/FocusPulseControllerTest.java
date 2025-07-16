package com.focuspulse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(FocusPulseController.class)
public class FocusPulseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void testWelcome() throws Exception {
        mockMvc.perform(post("/api/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to FocusPulseAPI!"));
    }

    @Test
    void testGetSettings() throws Exception {
        Map<String, Object> settings = new HashMap<>();
        settings.put("focus_minutes", 25);
        settings.put("break_minutes", 5);

        when(jdbcTemplate.queryForMap("SELECT focus_minutes, break_minutes FROM settings LIMIT 1"))
                .thenReturn(settings);

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.focus_minutes").value(25))
                .andExpect(jsonPath("$.break_minutes").value(5));
    }

    @Test
    void testUpdateSettings() throws Exception {
        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"focus_minutes\":30,\"break_minutes\":10}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Settings Updated!"));

        verify(jdbcTemplate).update("UPDATE settings SET focus_minutes = ?, break_minutes = ? WHERE id = 1", 30, 10);
    }

    @Test
    void testGetSessions() throws Exception {
        List<Map<String, Object>> sessions = new ArrayList<>();
        Map<String, Object> session = new HashMap<>();
        session.put("id", 1);
        session.put("session_date", "2025-07-16");
        session.put("focus_minutes", 45);
        sessions.add(session);

        when(jdbcTemplate.queryForList("SELECT * FROM sessions ORDER BY id DESC"))
                .thenReturn(sessions);

        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].session_date").value("2025-07-16"))
                .andExpect(jsonPath("$[0].focus_minutes").value(45));
    }

    @Test
    void testAddSession() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"session_date\":\"2025-07-16\",\"focus_minutes\":45}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Session Saved!"));

        verify(jdbcTemplate).update(
                "INSERT INTO sessions (session_date, focus_minutes) VALUES (?, ?)",
                "2025-07-16", 45
        );
    }
}
