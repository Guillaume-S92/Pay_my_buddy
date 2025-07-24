package com.paymybuddy.repository;

import com.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderId(Integer senderId);
    List<Transaction> findByReceiverId(Integer receiverId);
}
