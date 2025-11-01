package com.paymybuddy.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Transaction
 */
class TransactionTest {

    private Transaction transaction;
    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        sender = new User(1, "sender@example.com", "sender", "password");
        receiver = new User(2, "receiver@example.com", "receiver", "password");
    }

    /**
     * Test de création d'une transaction avec le constructeur par défaut
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getSender());
        assertNull(transaction.getReceiver());
        assertEquals(BigDecimal.ZERO, transaction.getAmount());
        assertNull(transaction.getDescription());
    }


    /**
     * Test de création d'une transaction avec le constructeur complet
     */
    @Test
    void testParameterizedConstructor() {
        BigDecimal amount = BigDecimal.valueOf(100.0);
        Transaction transactionWithParams = new Transaction(1, sender, receiver, amount, "Test transaction");

        assertEquals(1, transactionWithParams.getId());
        assertEquals(sender, transactionWithParams.getSender());
        assertEquals(receiver, transactionWithParams.getReceiver());
        assertEquals(amount, transactionWithParams.getAmount());
        assertEquals("Test transaction", transactionWithParams.getDescription());
    }


    /**
     * Test des setters et getters pour l'ID
     */
    @Test
    void testIdSetterAndGetter() {
        transaction.setId(1);
        assertEquals(1, transaction.getId());
    }

    /**
     * Test des setters et getters pour l'expéditeur
     */
    @Test
    void testSenderSetterAndGetter() {
        transaction.setSender(sender);
        assertEquals(sender, transaction.getSender());
    }

    /**
     * Test des setters et getters pour le destinataire
     */
    @Test
    void testReceiverSetterAndGetter() {
        transaction.setReceiver(receiver);
        assertEquals(receiver, transaction.getReceiver());
    }

    /**
     * Test des setters et getters pour le montant
     */
    @Test
    void testAmountSetterAndGetter() {
        BigDecimal amount = BigDecimal.valueOf(250.50);
        transaction.setAmount(amount);
        assertEquals(amount, transaction.getAmount());
    }


    /**
     * Test des setters et getters pour la description
     */
    @Test
    void testDescriptionSetterAndGetter() {
        String description = "Payment for services";
        transaction.setDescription(description);
        assertEquals(description, transaction.getDescription());
    }

    /**
     * Test de modification de tous les attributs
     */
    @Test
    void testSetAllAttributes() {
        transaction.setId(3);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(BigDecimal.valueOf(500.0));
        transaction.setDescription("Updated description");

        assertEquals(3, transaction.getId());
        assertEquals(sender, transaction.getSender());
        assertEquals(receiver, transaction.getReceiver());
        assertEquals(BigDecimal.valueOf(500.0), transaction.getAmount());
        assertEquals("Updated description", transaction.getDescription());
    }

    /**
     * Test avec un montant négatif
     */
    @Test
    void testNegativeAmount() {
        transaction.setAmount(BigDecimal.valueOf(-100.0));
        assertEquals(BigDecimal.valueOf(-100.0), transaction.getAmount());
    }

    /**
     * Test avec une description vide
     */
    @Test
    void testEmptyDescription() {
        transaction.setDescription("");
        assertEquals("", transaction.getDescription());
    }
}
