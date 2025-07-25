package com.paymybuddy.service;

import com.paymybuddy.model.UserConnection;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnectionId;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConnectionService {
    private final UserConnectionRepository userConnectionRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserConnectionService(UserConnectionRepository userConnectionRepository, UserRepository userRepository) {
        this.userConnectionRepository = userConnectionRepository;
        this.userRepository = userRepository;
    }

    public UserConnection createConnection(UserConnection connection) {
        return userConnectionRepository.save(connection);
    }

    public List<UserConnection> getConnectionsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userConnectionRepository.findByUser(user);
    }

    public List<UserConnection> getConnectionsByConnection(Integer connectionId) {
        User user = userRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userConnectionRepository.findByConnection(user);
    }

    public void deleteConnection(Integer userId, Integer connectionId) {
        UserConnectionId id = new UserConnectionId(userId, connectionId);
        userConnectionRepository.deleteById(id);
    }

}
