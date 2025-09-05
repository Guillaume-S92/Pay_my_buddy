package com.paymybuddy.repository;

import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.model.UserConnectionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour UserConnectionRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class UserConnectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    private User user1;
    private User user2;
    private User user3;
    private UserConnection userConnection;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        user1.setPassword("password");
        user1 = entityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setPassword("password");
        user2 = entityManager.persistAndFlush(user2);

        user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setUsername("user3");
        user3.setPassword("password");
        user3 = entityManager.persistAndFlush(user3);

        userConnection = new UserConnection();
        userConnection.setUser(user1);
        userConnection.setConnection(user2);
    }

    /**
     * Test de sauvegarde d'une connexion utilisateur
     */
    @Test
    void testSaveUserConnection() {
        UserConnection savedConnection = userConnectionRepository.save(userConnection);

        assertNotNull(savedConnection);
        assertEquals(user1.getId(), savedConnection.getUser().getId());
        assertEquals(user2.getId(), savedConnection.getConnection().getId());
    }

    /**
     * Test de recherche de connexions par utilisateur
     */
    @Test
    void testFindByUser() {
        entityManager.persistAndFlush(userConnection);

        List<UserConnection> connections = userConnectionRepository.findByUser(user1);

        assertEquals(1, connections.size());
        assertEquals(user1.getId(), connections.get(0).getUser().getId());
        assertEquals(user2.getId(), connections.get(0).getConnection().getId());
    }

    /**
     * Test de recherche de connexions par connexion
     */
    @Test
    void testFindByConnection() {
        entityManager.persistAndFlush(userConnection);

        List<UserConnection> connections = userConnectionRepository.findByConnection(user2);

        assertEquals(1, connections.size());
        assertEquals(user1.getId(), connections.get(0).getUser().getId());
        assertEquals(user2.getId(), connections.get(0).getConnection().getId());
    }

    /**
     * Test de recherche par utilisateur sans connexions
     */
    @Test
    void testFindByUserWhenNoConnections() {
        List<UserConnection> connections = userConnectionRepository.findByUser(user3);

        assertTrue(connections.isEmpty());
    }

    /**
     * Test de recherche par connexion sans connexions
     */
    @Test
    void testFindByConnectionWhenNoConnections() {
        List<UserConnection> connections = userConnectionRepository.findByConnection(user3);

        assertTrue(connections.isEmpty());
    }

    /**
     * Test de recherche par ID composite
     */
    @Test
    void testFindById() {
        UserConnection savedConnection = entityManager.persistAndFlush(userConnection);
        UserConnectionId id = new UserConnectionId(user1.getId(), user2.getId());

        Optional<UserConnection> foundConnection = userConnectionRepository.findById(id);

        assertTrue(foundConnection.isPresent());
        assertEquals(user1.getId(), foundConnection.get().getUser().getId());
        assertEquals(user2.getId(), foundConnection.get().getConnection().getId());
    }

    /**
     * Test de suppression par ID composite
     */
    @Test
    void testDeleteById() {
        entityManager.persistAndFlush(userConnection);
        UserConnectionId id = new UserConnectionId(user1.getId(), user2.getId());

        userConnectionRepository.deleteById(id);

        Optional<UserConnection> deletedConnection = userConnectionRepository.findById(id);
        assertFalse(deletedConnection.isPresent());
    }

    /**
     * Test de multiples connexions pour un utilisateur
     */
    @Test
    void testMultipleConnectionsForUser() {
        UserConnection connection2 = new UserConnection();
        connection2.setUser(user1);
        connection2.setConnection(user3);

        entityManager.persistAndFlush(userConnection);
        entityManager.persistAndFlush(connection2);

        List<UserConnection> connections = userConnectionRepository.findByUser(user1);

        assertEquals(2, connections.size());
    }

    /**
     * Test de récupération de toutes les connexions
     */
    @Test
    void testFindAll() {
        UserConnection connection2 = new UserConnection();
        connection2.setUser(user2);
        connection2.setConnection(user3);

        entityManager.persistAndFlush(userConnection);
        entityManager.persistAndFlush(connection2);

        List<UserConnection> allConnections = userConnectionRepository.findAll();

        assertEquals(2, allConnections.size());
    }

    /**
     * Test de connexion bidirectionnelle
     */
    @Test
    void testBidirectionalConnection() {
        UserConnection reverseConnection = new UserConnection();
        reverseConnection.setUser(user2);
        reverseConnection.setConnection(user1);

        entityManager.persistAndFlush(userConnection);
        entityManager.persistAndFlush(reverseConnection);

        List<UserConnection> user1Connections = userConnectionRepository.findByUser(user1);
        List<UserConnection> user2Connections = userConnectionRepository.findByUser(user2);

        assertEquals(1, user1Connections.size());
        assertEquals(1, user2Connections.size());
    }
}
