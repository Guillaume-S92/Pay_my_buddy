package com.paymybuddy.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    // Constructeur par défaut
    public Transaction() {
        this.amount = BigDecimal.ZERO; // Initialise à 0.00 pour éviter les null
    }

    // Constructeur avec paramètres
    public Transaction(Integer id, User sender, User receiver, BigDecimal amount, String description) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.description = description;
    }

    // Getters et setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
