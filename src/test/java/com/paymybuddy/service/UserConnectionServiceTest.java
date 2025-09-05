package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.model.UserConnectionId;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserConnectionService avec mocks
 */
@ExtendWith(MockitoExtension.class)
class UserConnectionServiceTest {

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserConnectionService userConnectionService;

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
     * Test de création d'une connexion
     */
    @Test
    void testCreateConnection() {
        when(userConnectionRepository.save(any(UserConnection.class))).thenReturn(testConnection);

        UserConnection result = userConnectionService.createConnection(testConnection);

        assertNotNull(result);
        assertEquals(testConnection, result);
        verify(userConnectionRepository).save(testConnection);
    }

    /**
     * Test de récupération des connexions par utilisateur existant
     */
    @Test
    void testGetConnectionsByUserWhenExists() {
        List<UserConnection> connections = Arrays.asList(testConnection);
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userConnectionRepository.findByUser(user1)).thenReturn(connections);

        List<UserConnection> result = userConnectionService.getConnectionsByUser(1);

        assertEquals(1, result.size());
        assertEquals(testConnection, result.get(0));
        verify(userRepository).findById(1);
        verify(userConnectionRepository).findByUser(user1);
    }

    /**
     * Test de récupération des connexions par utilisateur inexistant
     */
    @Test
    void testGetConnectionsByUserWhenNotExists() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userConnectionService.getConnectionsByUser(999);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(999);
        verify(userConnectionRepository, never()).findByUser(any());
    }

    /**
     * Test de récupération des connexions par connexion existante
     */
    @Test
    void testGetConnectionsByConnectionWhenExists() {
        List<UserConnection> connections = Arrays.asList(testConnection);
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        when(userConnectionRepository.findByConnection(user2)).thenReturn(connections);

        List<UserConnection> result = userConnectionService.getConnectionsByConnection(2);

        assertEquals(1, result.size());
        assertEquals(testConnection, result.get(0));
        verify(userRepository).findById(2);
        verify(userConnectionRepository).findByConnection(user2);
    }

    /**
     * Test de récupération des connexions par connexion inexistante
     */
    @Test
    void testGetConnectionsByConnectionWhenNotExists() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userConnectionService.getConnectionsByConnection(999);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(999);
        verify(userConnectionRepository, never()).findByConnection(any());
    }

    /**
     * Test de suppression d'une connexion
     */
    @Test
    void testDeleteConnection() {
        UserConnectionId expectedId = new UserConnectionId(1, 2);
        doNothing().when(userConnectionRepository).deleteById(any(UserConnectionId.class));

        userConnectionService.deleteConnection(1, 2);

        verify(userConnectionRepository).deleteById(argThat(id ->
            id.getUser().equals(1) && id.getConnection().equals(2)));
    }

    /**
     * Test de récupération de connexions vides par utilisateur
     */
    @Test
    void testGetConnectionsByUserWhenEmpty() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userConnectionRepository.findByUser(user1)).thenReturn(Arrays.asList());

        List<UserConnection> result = userConnectionService.getConnectionsByUser(1);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(1);
        verify(userConnectionRepository).findByUser(user1);
    }

    /**
     * Test de récupération de connexions vides par connexion
     */
    @Test
    void testGetConnectionsByConnectionWhenEmpty() {
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        when(userConnectionRepository.findByConnection(user2)).thenReturn(Arrays.asList());

        List<UserConnection> result = userConnectionService.getConnectionsByConnection(2);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(2);
        verify(userConnectionRepository).findByConnection(user2);
    }

    /**
     * Test de création d'une connexion avec des utilisateurs null
     */
    @Test
    void testCreateConnectionWithNullUsers() {
        UserConnection nullConnection = new UserConnection();
        when(userConnectionRepository.save(any(UserConnection.class))).thenReturn(nullConnection);

        UserConnection result = userConnectionService.createConnection(nullConnection);

        assertNotNull(result);
        verify(userConnectionRepository).save(nullConnection);
    }

    /**
     * Test de suppression avec des IDs null
     */
    @Test
    void testDeleteConnectionWithNullIds() {
        userConnectionService.deleteConnection(null, null);

        verify(userConnectionRepository).deleteById(argThat(id ->
            id.getUser() == null && id.getConnection() == null));
    }

    /**
     * Test de multiples connexions pour un utilisateur
     */
    @Test
    void testMultipleConnectionsForUser() {
        User user3 = new User();
        user3.setId(3);
        UserConnection connection2 = new UserConnection();
        connection2.setUser(user1);
        connection2.setConnection(user3);

        List<UserConnection> connections = Arrays.asList(testConnection, connection2);
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userConnectionRepository.findByUser(user1)).thenReturn(connections);

        List<UserConnection> result = userConnectionService.getConnectionsByUser(1);

        assertEquals(2, result.size());
        verify(userRepository).findById(1);
        verify(userConnectionRepository).findByUser(user1);
    }
}
