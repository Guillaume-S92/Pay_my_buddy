package com.paymybuddy.repository;

import com.paymybuddy.model.User;
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
 * Tests d'intégration pour UserRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
    }

    /**
     * Test de sauvegarde d'un utilisateur
     */
    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
    }

    /**
     * Test de recherche d'un utilisateur par email existant
     */
    @Test
    void testFindByEmailWhenExists() {
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    /**
     * Test de recherche d'un utilisateur par email inexistant
     */
    @Test
    void testFindByEmailWhenNotExists() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    /**
     * Test de recherche par ID existant
     */
    @Test
    void testFindByIdWhenExists() {
        User savedUser = entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    /**
     * Test de recherche par ID inexistant
     */
    @Test
    void testFindByIdWhenNotExists() {
        Optional<User> foundUser = userRepository.findById(999);

        assertFalse(foundUser.isPresent());
    }

    /**
     * Test de récupération de tous les utilisateurs
     */
    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setPassword("password2");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    /**
     * Test de suppression d'un utilisateur
     */
    @Test
    void testDeleteUser() {
        User savedUser = entityManager.persistAndFlush(testUser);

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Test de mise à jour d'un utilisateur
     */
    @Test
    void testUpdateUser() {
        User savedUser = entityManager.persistAndFlush(testUser);

        savedUser.setUsername("updateduser");
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);

        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    /**
     * Test de l'unicité de l'email (constraint violation attendue)
     */
    @Test
    void testEmailUniqueness() {
        entityManager.persistAndFlush(testUser);

        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com"); // même email
        duplicateUser.setUsername("anotheruser");
        duplicateUser.setPassword("password456");

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }
}
