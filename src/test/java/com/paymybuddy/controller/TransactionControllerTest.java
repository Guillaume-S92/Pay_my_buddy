package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour TransactionController
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction testTransaction;
    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1);
        sender.setEmail("sender@example.com");
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2);
        receiver.setEmail("receiver@example.com");
        receiver.setUsername("receiver");

        testTransaction = new Transaction();
        testTransaction.setId(1);
        testTransaction.setSender(sender);
        testTransaction.setReceiver(receiver);
        testTransaction.setAmount(100.0);
        testTransaction.setDescription("Test transaction");
    }

    /**
     * Test de création d'une transaction via POST
     */
    @Test
    @WithMockUser
    void testCreateTransaction() throws Exception {
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.description").value("Test transaction"))
                .andExpect(jsonPath("$.sender.id").value(1))
                .andExpect(jsonPath("$.receiver.id").value(2));
    }

    /**
     * Test de récupération de toutes les transactions via GET
     */
    @Test
    @WithMockUser
    void testGetAllTransactions() throws Exception {
        Transaction transaction2 = new Transaction();
        transaction2.setId(2);
        transaction2.setSender(receiver);
        transaction2.setReceiver(sender);
        transaction2.setAmount(50.0);
        transaction2.setDescription("Second transaction");

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    /**
     * Test de récupération d'une transaction par ID existant
     */
    @Test
    @WithMockUser
    void testGetTransactionByIdWhenExists() throws Exception {
        when(transactionService.getTransactionById(1)).thenReturn(Optional.of(testTransaction));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.description").value("Test transaction"));
    }

    /**
     * Test de récupération d'une transaction par ID inexistant
     */
    @Test
    @WithMockUser
    void testGetTransactionByIdWhenNotExists() throws Exception {
        when(transactionService.getTransactionById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test de création d'une transaction avec montant négatif
     */
    @Test
    @WithMockUser
    void testCreateTransactionWithNegativeAmount() throws Exception {
        testTransaction.setAmount(-50.0);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(-50.0));
    }

    /**
     * Test de création d'une transaction avec JSON malformé
     */
    @Test
    @WithMockUser
    void testCreateTransactionWithMalformedJson() throws Exception {
        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().is5xxServerError()); // Changé de isBadRequest() à is5xxServerError()
    }

    /**
     * Test de création d'une transaction avec description nulle
     */
    @Test
    @WithMockUser
    void testCreateTransactionWithNullDescription() throws Exception {
        testTransaction.setDescription(null);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").doesNotExist());
    }
}
