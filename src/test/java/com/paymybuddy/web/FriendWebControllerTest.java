package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendWebControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserConnectionService userConnectionService;
    @Mock
    private AuthUtil authUtil;
    @Mock
    private Model model;
    @InjectMocks
    private FriendWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new FriendWebController(userService, userConnectionService, authUtil);
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testFriendPage() {
        User user = new User();
        user.setId(1);
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(userConnectionService.getConnectionsByUser(1)).thenReturn(Collections.emptyList());
        String view = controller.friendPage(model);
        verify(model).addAttribute(eq("friends"), any());
        assertEquals("friends", view);
    }

    @Test
    void testShowAddFriendForm() {
        String view = controller.showAddFriendForm(model);
        verify(model).addAttribute("email", "");
        verify(model).addAttribute("msg", "");
        verify(model).addAttribute("error", "");
        assertEquals("add-friend", view);
    }
}
