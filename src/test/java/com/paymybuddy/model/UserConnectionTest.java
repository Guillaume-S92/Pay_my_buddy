package com.paymybuddy.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe UserConnection
 */
class UserConnectionTest {

    private UserConnection userConnection;
    private User user;
    private User connection;

    @BeforeEach
    void setUp() {
        userConnection = new UserConnection();
        user = new User(1, "user@example.com", "user1", "password");
        connection = new User(2, "connection@example.com", "user2", "password");
    }

    /**
     * Test de création d'une connexion utilisateur avec le constructeur par défaut
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(userConnection);
        assertNull(userConnection.getUser());
        assertNull(userConnection.getConnection());
    }

    /**
     * Test de création d'une connexion utilisateur avec le constructeur paramétré
     */
    @Test
    void testParameterizedConstructor() {
        UserConnection connectionWithParams = new UserConnection(user, connection);

        assertEquals(user, connectionWithParams.getUser());
        assertEquals(connection, connectionWithParams.getConnection());
    }

    /**
     * Test des setters et getters pour l'utilisateur
     */
    @Test
    void testUserSetterAndGetter() {
        userConnection.setUser(user);
        assertEquals(user, userConnection.getUser());
    }

    /**
     * Test des setters et getters pour la connexion
     */
    @Test
    void testConnectionSetterAndGetter() {
        userConnection.setConnection(connection);
        assertEquals(connection, userConnection.getConnection());
    }

    /**
     * Test de modification des deux attributs
     */
    @Test
    void testSetBothAttributes() {
        userConnection.setUser(user);
        userConnection.setConnection(connection);

        assertEquals(user, userConnection.getUser());
        assertEquals(connection, userConnection.getConnection());
    }
}
