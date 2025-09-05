package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour HomeController
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUtil authUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
    }

    /**
     * Test d'affichage de la page d'accueil avec utilisateur connecté
     */
    @Test
    @WithMockUser
    void testHomeWithAuthenticatedUser() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", testUser));
    }

    /**
     * Test d'affichage de la page d'accueil sans utilisateur connecté (exception)
     */
    @Test
    @WithMockUser
    void testHomeWithoutAuthenticatedUser() throws Exception {
        when(authUtil.getCurrentUser()).thenThrow(new RuntimeException("Utilisateur non connecté"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("user", (Object) null));
    }

    /**
     * Test d'affichage de la page d'accueil avec AuthUtil retournant null
     */
    @Test
    @WithMockUser
    void testHomeWithNullUser() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("user", (Object) null));
    }
}
