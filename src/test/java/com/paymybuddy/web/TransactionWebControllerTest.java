package com.paymybuddy.web;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.UserConnectionService;
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

class TransactionWebControllerTest {
    @Mock
    private TransactionService transactionService;
    @Mock
    private UserService userService;
    @Mock
    private UserConnectionService userConnectionService;
    @Mock
    private AuthUtil authUtil;
    @Mock
    private Model model;
    @InjectMocks
    private TransactionWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new TransactionWebController(transactionService, userService, userConnectionService, authUtil);
    }

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testListTransactions() {
        User user = new User();
        user.setId(1);
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(transactionService.getTransactionsBySender(1)).thenReturn(Collections.emptyList());
        String view = controller.listTransactions(model);
        verify(model).addAttribute(eq("transactions"), any());
        assertEquals("transactions", view);
    }

    @Test
    void testShowTransactionForm() {
        User user = new User();
        user.setId(1);
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(userConnectionService.getConnectionsByUser(1)).thenReturn(Collections.emptyList());
        String view = controller.showTransactionForm(model);
        verify(model).addAttribute(eq("transaction"), any(Transaction.class));
        verify(model).addAttribute(eq("users"), any());
        assertEquals("transaction-form", view);
    }
}
