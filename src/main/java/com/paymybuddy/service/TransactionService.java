package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        // 1. Validation du montant
        BigDecimal amount = transaction.getAmount();
        if (amount == null) {
            throw new IllegalArgumentException("Le montant ne peut pas être nul.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif.");
        }

        // 2. Récupération du vrai sender en base
        Integer senderId = transaction.getSender().getId();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable."));

        // 3. Récupération du vrai receiver en base
        Integer receiverId = transaction.getReceiver().getId();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable."));

        // 4. Interdiction d'envoyer à soi-même
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Impossible d’envoyer de l’argent à soi-même.");
        }

        // 5. On remplace dans l'objet transaction les versions 'données par le formulaire'
        //    par les vraies entités persistées (gérées par JPA)
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        // 6. Sauvegarde
        return transactionRepository.save(transaction);
        // Grâce à @Transactional :
        // - si une exception est levée à un moment dans cette méthode,
        //   la transaction DB est annulée (rollback).
    }

    public List<Transaction> getTransactionsBySender(Integer senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    public List<Transaction> getTransactionsByReceiver(Integer receiverId) {
        return transactionRepository.findByReceiverId(receiverId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionRepository.findById(id);
    }
}
