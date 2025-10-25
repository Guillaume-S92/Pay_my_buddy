package com.paymybuddy.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityConfig config;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testPasswordEncoderBean() {
        BCryptPasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        String hash = encoder.encode("test");
        assertTrue(encoder.matches("test", hash));
    }

    @Test
    void testPublicUrlsAccessible() throws Exception {
        mockMvc.perform(get("/register")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void testProtectedUrlRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/transactions")).andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }
}
