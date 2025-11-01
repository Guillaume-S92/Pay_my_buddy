package com.paymybuddy.repository;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour TransactionRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private User sender;
    private User receiver;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setUsername("sender");
        sender.setPassword("password");
        sender = entityManager.persistAndFlush(sender);

        receiver = new User();
        receiver.setEmail("receiver@example.com");
        receiver.setUsername("receiver");
        receiver.setPassword("password");
        receiver = entityManager.persistAndFlush(receiver);

        testTransaction = new Transaction();
        testTransaction.setSender(sender);
        testTransaction.setReceiver(receiver);
        testTransaction.setAmount(BigDecimal.valueOf(100.0));
        testTransaction.setDescription("Test transaction");
    }

    /**
     * Test de sauvegarde d'une transaction
     */
    @Test
    void testSaveTransaction() {
        Transaction savedTransaction = transactionRepository.save(testTransaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(testTransaction.getAmount(), savedTransaction.getAmount());
        assertEquals(testTransaction.getDescription(), savedTransaction.getDescription());
        assertEquals(sender.getId(), savedTransaction.getSender().getId());
        assertEquals(receiver.getId(), savedTransaction.getReceiver().getId());
    }

    /**
     * Test de recherche de transactions par ID de l'expéditeur
     */
    @Test
    void testFindBySenderId() {
        entityManager.persistAndFlush(testTransaction);

        List<Transaction> transactions = transactionRepository.findBySenderId(sender.getId());

        assertEquals(1, transactions.size());
        assertEquals(sender.getId(), transactions.get(0).getSender().getId());
    }

    /**
     * Test de recherche de transactions par ID du destinataire
     */
    @Test
    void testFindByReceiverId() {
        entityManager.persistAndFlush(testTransaction);

        List<Transaction> transactions = transactionRepository.findByReceiverId(receiver.getId());

        assertEquals(1, transactions.size());
        assertEquals(receiver.getId(), transactions.get(0).getReceiver().getId());
    }

    /**
     * Test de recherche par ID inexistant d'expéditeur
     */
    @Test
    void testFindBySenderIdWhenNoTransactions() {
        List<Transaction> transactions = transactionRepository.findBySenderId(999);

        assertTrue(transactions.isEmpty());
    }

    /**
     * Test de recherche par ID inexistant de destinataire
     */
    @Test
    void testFindByReceiverIdWhenNoTransactions() {
        List<Transaction> transactions = transactionRepository.findByReceiverId(999);

        assertTrue(transactions.isEmpty());
    }

    /**
     * Test de récupération de toutes les transactions
     */
    @Test
    void testFindAll() {
        Transaction transaction1 = new Transaction();
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setAmount(BigDecimal.valueOf(50.0));
        transaction1.setDescription("Transaction 1");

        Transaction transaction2 = new Transaction();
        transaction2.setSender(receiver);
        transaction2.setReceiver(sender);
        transaction2.setAmount(BigDecimal.valueOf(75.0));
        transaction2.setDescription("Transaction 2");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        List<Transaction> transactions = transactionRepository.findAll();

        assertEquals(2, transactions.size());
    }

    /**
     * Test de recherche par ID de transaction
     */
    @Test
    void testFindById() {
        Transaction savedTransaction = entityManager.persistAndFlush(testTransaction);

        Optional<Transaction> foundTransaction = transactionRepository.findById(savedTransaction.getId());

        assertTrue(foundTransaction.isPresent());
        assertEquals(savedTransaction.getId(), foundTransaction.get().getId());
    }

    /**
     * Test de suppression d'une transaction
     */
    @Test
    void testDeleteTransaction() {
        Transaction savedTransaction = entityManager.persistAndFlush(testTransaction);

        transactionRepository.deleteById(savedTransaction.getId());

        Optional<Transaction> deletedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertFalse(deletedTransaction.isPresent());
    }

    /**
     * Test de plusieurs transactions pour le même expéditeur
     */
    @Test
    void testMultipleTransactionsBySameSender() {
        Transaction transaction2 = new Transaction();
        transaction2.setSender(sender);
        transaction2.setReceiver(receiver);
        transaction2.setAmount(BigDecimal.valueOf(200.0));
        transaction2.setDescription("Second transaction");

        entityManager.persistAndFlush(testTransaction);
        entityManager.persistAndFlush(transaction2);

        List<Transaction> transactions = transactionRepository.findBySenderId(sender.getId());

        assertEquals(2, transactions.size());
    }

    /**
     * Test de transaction avec montant zéro
     */
    @Test
    void testTransactionWithZeroAmount() {
        testTransaction.setAmount(BigDecimal.valueOf(0.0));
        Transaction savedTransaction = transactionRepository.save(testTransaction);

        assertEquals(BigDecimal.valueOf(0.0), savedTransaction.getAmount());
    }
}
