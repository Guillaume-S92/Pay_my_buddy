package com.paymybuddy.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe User
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    /**
     * Test de création d'un utilisateur avec le constructeur par défaut
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
    }

    /**
     * Test de création d'un utilisateur avec le constructeur complet
     */
    @Test
    void testParameterizedConstructor() {
        User userWithParams = new User(1, "test@example.com", "testuser", "password123");

        assertEquals(1, userWithParams.getId());
        assertEquals("test@example.com", userWithParams.getEmail());
        assertEquals("testuser", userWithParams.getUsername());
        assertEquals("password123", userWithParams.getPassword());
    }

    /**
     * Test des setters et getters pour l'ID
     */
    @Test
    void testIdSetterAndGetter() {
        user.setId(1);
        assertEquals(1, user.getId());
    }

    /**
     * Test des setters et getters pour l'email
     */
    @Test
    void testEmailSetterAndGetter() {
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    /**
     * Test des setters et getters pour le nom d'utilisateur
     */
    @Test
    void testUsernameSetterAndGetter() {
        String username = "testuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    /**
     * Test des setters et getters pour le mot de passe
     */
    @Test
    void testPasswordSetterAndGetter() {
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    /**
     * Test de modification de tous les attributs
     */
    @Test
    void testSetAllAttributes() {
        user.setId(2);
        user.setEmail("new@example.com");
        user.setUsername("newuser");
        user.setPassword("newpassword");

        assertEquals(2, user.getId());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
    }
}
