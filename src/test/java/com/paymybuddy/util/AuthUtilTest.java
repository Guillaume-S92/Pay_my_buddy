package com.paymybuddy.util;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AuthUtil
 */
@ExtendWith(MockitoExtension.class)
class AuthUtilTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
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
     * Test de récupération de l'utilisateur connecté avec succès
     */
    @Test
    void testGetCurrentUserSuccess() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            User result = authUtil.getCurrentUser();

            assertNotNull(result);
            assertEquals(testUser, result);
            assertEquals("test@example.com", result.getEmail());
        }
    }

    /**
     * Test de récupération de l'utilisateur connecté quand l'utilisateur n'existe pas
     */
    @Test
    void testGetCurrentUserWhenUserNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("nonexistent@example.com");
            when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> authUtil.getCurrentUser());
        }
    }

    /**
     * Test de récupération de l'utilisateur connecté avec authentification null
     */
    @Test
    void testGetCurrentUserWithNullAuthentication() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThrows(Exception.class, () -> authUtil.getCurrentUser());
        }
    }

    /**
     * Test de récupération de l'utilisateur connecté avec nom null
     */
    @Test
    void testGetCurrentUserWithNullName() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(null);
            when(userService.getUserByEmail(null)).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> authUtil.getCurrentUser());
        }
    }
}
