package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AuthController
 */
@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
    }

    /**
     * Test d'affichage de la page de connexion simple
     */
    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("msg"));
    }

    /**
     * Test d'affichage de la page de connexion avec erreur
     */
    @Test
    void testLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Identifiants incorrects"));
    }

    /**
     * Test d'affichage de la page de connexion après déconnexion
     */
    @Test
    void testLoginPageWithLogout() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("msg", "Déconnexion réussie !"));
    }

    /**
     * Test d'affichage du formulaire d'inscription
     */
    @Test
    void testRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Test d'inscription avec succès
     */
    @Test
    void testRegisterSuccess() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/register")
                .param("email", "test@example.com")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Test d'inscription avec email déjà existant
     */
    @Test
    void testRegisterWithExistingEmail() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/register")
                .param("email", "test@example.com")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("error", "Email déjà utilisé !"));
    }

    /**
     * Test d'inscription avec données vides
     */
    @Test
    void testRegisterWithEmptyData() throws Exception {
        when(userService.getUserByEmail("")).thenReturn(Optional.empty());

        mockMvc.perform(post("/register")
                .param("email", "")
                .param("username", "")
                .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
