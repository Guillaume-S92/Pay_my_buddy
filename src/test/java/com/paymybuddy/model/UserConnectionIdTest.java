package com.paymybuddy.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe UserConnectionId
 */
class UserConnectionIdTest {

    private UserConnectionId userConnectionId;

    @BeforeEach
    void setUp() {
        userConnectionId = new UserConnectionId();
    }

    /**
     * Test de création d'un ID de connexion avec le constructeur par défaut
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(userConnectionId);
        assertNull(userConnectionId.getUser());
        assertNull(userConnectionId.getConnection());
    }

    /**
     * Test de création d'un ID de connexion avec le constructeur paramétré
     */
    @Test
    void testParameterizedConstructor() {
        UserConnectionId idWithParams = new UserConnectionId(1, 2);

        assertEquals(1, idWithParams.getUser());
        assertEquals(2, idWithParams.getConnection());
    }

    /**
     * Test des setters et getters pour l'ID utilisateur
     */
    @Test
    void testUserIdSetterAndGetter() {
        userConnectionId.setUser(5);
        assertEquals(5, userConnectionId.getUser());
    }

    /**
     * Test des setters et getters pour l'ID de connexion
     */
    @Test
    void testConnectionIdSetterAndGetter() {
        userConnectionId.setConnection(10);
        assertEquals(10, userConnectionId.getConnection());
    }

    /**
     * Test de la méthode equals avec des objets identiques
     */
    @Test
    void testEqualsWithSameObject() {
        assertTrue(userConnectionId.equals(userConnectionId));
    }

    /**
     * Test de la méthode equals avec des objets équivalents
     */
    @Test
    void testEqualsWithEquivalentObjects() {
        UserConnectionId id1 = new UserConnectionId(1, 2);
        UserConnectionId id2 = new UserConnectionId(1, 2);

        assertTrue(id1.equals(id2));
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    /**
     * Test de la méthode equals avec des objets différents
     */
    @Test
    void testEqualsWithDifferentObjects() {
        UserConnectionId id1 = new UserConnectionId(1, 2);
        UserConnectionId id2 = new UserConnectionId(2, 1);

        assertFalse(id1.equals(id2));
    }

    /**
     * Test de la méthode equals avec un objet null
     */
    @Test
    void testEqualsWithNull() {
        UserConnectionId id = new UserConnectionId(1, 2);
        assertFalse(id.equals(null));
    }

    /**
     * Test de la méthode equals avec un objet d'une classe différente
     */
    @Test
    void testEqualsWithDifferentClass() {
        UserConnectionId id = new UserConnectionId(1, 2);
        String differentObject = "test";
        assertFalse(id.equals(differentObject));
    }

    /**
     * Test de la cohérence des hashCodes pour des objets équivalents
     */
    @Test
    void testHashCodeConsistency() {
        UserConnectionId id1 = new UserConnectionId(1, 2);
        UserConnectionId id2 = new UserConnectionId(1, 2);

        assertEquals(id1.hashCode(), id2.hashCode());
    }

    /**
     * Test que des objets différents ont des hashCodes différents (dans la plupart des cas)
     */
    @Test
    void testHashCodeForDifferentObjects() {
        UserConnectionId id1 = new UserConnectionId(1, 2);
        UserConnectionId id2 = new UserConnectionId(2, 1);

        assertNotEquals(id1.hashCode(), id2.hashCode());
    }
}
