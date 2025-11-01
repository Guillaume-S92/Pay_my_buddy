package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour TransactionService avec mocks
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

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
        testTransaction.setAmount(BigDecimal.valueOf(100.0));
        testTransaction.setDescription("Test transaction");
    }

    /**
     * Test de création d'une transaction avec utilisateurs existants
     */
    @Test
    void testCreateTransactionSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.createTransaction(testTransaction);

        assertNotNull(result);
        assertEquals(testTransaction, result);
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
        verify(transactionRepository).save(testTransaction);
    }

    /**
     * Test de création d'une transaction avec expéditeur inexistant
     */
    @Test
    void testCreateTransactionWithInvalidSender() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });

        assertEquals("Sender not found", exception.getMessage());
        verify(userRepository).findById(1);
        verify(userRepository, never()).findById(2);
        verify(transactionRepository, never()).save(any());
    }

    /**
     * Test de création d'une transaction avec destinataire inexistant
     */
    @Test
    void testCreateTransactionWithInvalidReceiver() {
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(testTransaction);
        });

        assertEquals("Receiver not found", exception.getMessage());
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
        verify(transactionRepository, never()).save(any());
    }

    /**
     * Test de récupération des transactions par expéditeur
     */
    @Test
    void testGetTransactionsBySender() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findBySenderId(1)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsBySender(1);

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
        verify(transactionRepository).findBySenderId(1);
    }

    /**
     * Test de récupération des transactions par destinataire
     */
    @Test
    void testGetTransactionsByReceiver() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByReceiverId(2)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByReceiver(2);

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
        verify(transactionRepository).findByReceiverId(2);
    }

    /**
     * Test de récupération de toutes les transactions
     */
    @Test
    void testGetAllTransactions() {
        List<Transaction> transactions = Arrays.asList(testTransaction, new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    /**
     * Test de récupération d'une transaction par ID existant
     */
    @Test
    void testGetTransactionByIdWhenExists() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionService.getTransactionById(1);

        assertTrue(result.isPresent());
        assertEquals(testTransaction, result.get());
        verify(transactionRepository).findById(1);
    }

    /**
     * Test de récupération d'une transaction par ID inexistant
     */
    @Test
    void testGetTransactionByIdWhenNotExists() {
        when(transactionRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransactionById(999);

        assertFalse(result.isPresent());
        verify(transactionRepository).findById(999);
    }

    /**
     * Test de récupération de transactions par expéditeur sans résultats
     */
    @Test
    void testGetTransactionsBySenderWhenEmpty() {
        when(transactionRepository.findBySenderId(999)).thenReturn(Arrays.asList());

        List<Transaction> result = transactionService.getTransactionsBySender(999);

        assertTrue(result.isEmpty());
        verify(transactionRepository).findBySenderId(999);
    }

    /**
     * Test de récupération de transactions par destinataire sans résultats
     */
    @Test
    void testGetTransactionsByReceiverWhenEmpty() {
        when(transactionRepository.findByReceiverId(999)).thenReturn(Arrays.asList());

        List<Transaction> result = transactionService.getTransactionsByReceiver(999);

        assertTrue(result.isEmpty());
        verify(transactionRepository).findByReceiverId(999);
    }

    /**
     * Test que les utilisateurs sont bien récupérés et assignés lors de la création
     */
    @Test
    void testCreateTransactionSetsCorrectUsers() {
        User fullSender = new User(1, "sender@example.com", "sender", "password");
        User fullReceiver = new User(2, "receiver@example.com", "receiver", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(fullSender));
        when(userRepository.findById(2)).thenReturn(Optional.of(fullReceiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            assertEquals(fullSender, savedTransaction.getSender());
            assertEquals(fullReceiver, savedTransaction.getReceiver());
            return savedTransaction;
        });

        transactionService.createTransaction(testTransaction);

        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
        verify(transactionRepository).save(testTransaction);
    }
}
