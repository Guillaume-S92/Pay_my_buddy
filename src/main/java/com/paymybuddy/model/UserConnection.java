package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_connections")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
