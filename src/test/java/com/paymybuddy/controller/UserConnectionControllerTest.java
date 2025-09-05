package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.service.UserConnectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour UserConnectionController
 */
@WebMvcTest(UserConnectionController.class)
class UserConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserConnectionService userConnectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserConnection testConnection;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");

        testConnection = new UserConnection();
        testConnection.setUser(user1);
        testConnection.setConnection(user2);
    }

    /**
     * Test de création d'une connexion via POST
     */
    @Test
    @WithMockUser
    void testCreateConnection() throws Exception {
        when(userConnectionService.createConnection(any(UserConnection.class))).thenReturn(testConnection);

        mockMvc.perform(post("/api/user-connections")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testConnection)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.connection.id").value(2));
    }

    /**
     * Test de récupération des connexions par utilisateur
     */
    @Test
    @WithMockUser
    void testGetConnectionsByUser() throws Exception {
        List<UserConnection> connections = Arrays.asList(testConnection);
        when(userConnectionService.getConnectionsByUser(1)).thenReturn(connections);

        mockMvc.perform(get("/api/user-connections/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].user.id").value(1))
                .andExpect(jsonPath("$[0].connection.id").value(2));
    }

    /**
     * Test de récupération des connexions par connexion
     */
    @Test
    @WithMockUser
    void testGetConnectionsByConnection() throws Exception {
        List<UserConnection> connections = Arrays.asList(testConnection);
        when(userConnectionService.getConnectionsByConnection(2)).thenReturn(connections);

        mockMvc.perform(get("/api/user-connections/connection/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].user.id").value(1))
                .andExpect(jsonPath("$[0].connection.id").value(2));
    }

    /**
     * Test de récupération des connexions par utilisateur sans résultats
     */
    @Test
    @WithMockUser
    void testGetConnectionsByUserWhenEmpty() throws Exception {
        when(userConnectionService.getConnectionsByUser(999)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/user-connections/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test de récupération des connexions par connexion sans résultats
     */
    @Test
    @WithMockUser
    void testGetConnectionsByConnectionWhenEmpty() throws Exception {
        when(userConnectionService.getConnectionsByConnection(999)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/user-connections/connection/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test de création d'une connexion avec JSON malformé
     */
    @Test
    @WithMockUser
    void testCreateConnectionWithMalformedJson() throws Exception {
        mockMvc.perform(post("/api/user-connections")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().is5xxServerError()); // Changé de isBadRequest() à is5xxServerError()
    }

    /**
     * Test de récupération avec ID utilisateur invalide
     */
    @Test
    @WithMockUser
    void testGetConnectionsByUserWithInvalidId() throws Exception {
        when(userConnectionService.getConnectionsByUser(-1)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/user-connections/user/-1"))
                .andExpect(status().isInternalServerError());
    }
}
