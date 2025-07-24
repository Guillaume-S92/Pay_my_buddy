package com.paymybuddy.repository;

import com.paymybuddy.model.UserConnection;
import com.paymybuddy.model.UserConnectionId;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId> {
    List<UserConnection> findByUser(User user);
    List<UserConnection> findByConnection(User connection);
}
