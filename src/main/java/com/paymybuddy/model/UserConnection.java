package com.paymybuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_connections")
@IdClass(UserConnectionId.class)
public class UserConnection {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "connection_id")
    private User connection;

    // Constructeurs
    public UserConnection() {}

    public UserConnection(User user, User connection) {
        this.user = user;
        this.connection = connection;
    }

    // Getters et setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getConnection() { return connection; }
    public void setConnection(User connection) { this.connection = connection; }
}
