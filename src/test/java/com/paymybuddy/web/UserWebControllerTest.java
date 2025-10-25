package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserWebControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @InjectMocks
    private UserWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UserWebController(userService);
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testListUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        String view = controller.listUsers(model);
        verify(model).addAttribute(eq("users"), any());
        assertEquals("users", view);
    }

    @Test
    void testShowUserForm() {
        String view = controller.showUserForm(model);
        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("user-form", view);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        String view = controller.createUser(user);
        verify(userService).createUser(user);
        assertEquals("redirect:/users", view);
    }
}
