package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService avec mocks
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
    }

    /**
     * Test de création d'un utilisateur avec encodage du mot de passe
     */
    @Test
    void testCreateUser() {
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertEquals(testUser, result);
    }

    /**
     * Test de récupération de tous les utilisateurs
     */
    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    /**
     * Test de recherche d'utilisateur par email existant
     */
    @Test
    void testGetUserByEmailWhenExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail("test@example.com");
    }

    /**
     * Test de recherche d'utilisateur par email inexistant
     */
    @Test
    void testGetUserByEmailWhenNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    /**
     * Test de recherche d'utilisateur par ID existant
     */
    @Test
    void testGetUserByIdWhenExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(1);
    }

    /**
     * Test de recherche d'utilisateur par ID inexistant
     */
    @Test
    void testGetUserByIdWhenNotExists() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999);

        assertFalse(result.isPresent());
        verify(userRepository).findById(999);
    }

    /**
     * Test de chargement d'utilisateur par nom d'utilisateur (email) pour Spring Security
     */
    @Test
    void testLoadUserByUsernameWhenExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByEmail("test@example.com");
    }

    /**
     * Test de chargement d'utilisateur par nom d'utilisateur inexistant
     */
    @Test
    void testLoadUserByUsernameWhenNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    /**
     * Test de création d'utilisateur avec mot de passe null
     */
    @Test
    void testCreateUserWithNullPassword() {
        testUser.setPassword(null);
        when(passwordEncoder.encode(null)).thenReturn("encodedNull");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        verify(passwordEncoder).encode(null);
        verify(userRepository).save(testUser);
    }

    /**
     * Test de création d'utilisateur avec mot de passe vide
     */
    @Test
    void testCreateUserWithEmptyPassword() {
        testUser.setPassword("");
        when(passwordEncoder.encode("")).thenReturn("encodedEmpty");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        verify(passwordEncoder).encode("");
        verify(userRepository).save(testUser);
    }

    /**
     * Test que le mot de passe est bien modifié après encodage
     */
    @Test
    void testPasswordIsEncodedBeforeSaving() {
        String originalPassword = "password123";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(originalPassword);

        when(passwordEncoder.encode(originalPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(encodedPassword, user.getPassword());
            return user;
        });

        userService.createUser(testUser);

        verify(passwordEncoder).encode(originalPassword);
        verify(userRepository).save(testUser);
    }
}
