package com.paymybuddy.controller;

import com.paymybuddy.model.UserConnection;
import com.paymybuddy.service.UserConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-connections")
public class UserConnectionController {
    private final UserConnectionService userConnectionService;

    @Autowired
    public UserConnectionController(UserConnectionService userConnectionService) {
        this.userConnectionService = userConnectionService;
    }

    @PostMapping
    public UserConnection createConnection(@RequestBody UserConnection connection) {
        return userConnectionService.createConnection(connection);
    }

    @GetMapping("/user/{userId}")
    public List<UserConnection> getConnectionsByUser(@PathVariable Integer userId) {
        return userConnectionService.getConnectionsByUser(userId);
    }

    @GetMapping("/connection/{connectionId}")
    public List<UserConnection> getConnectionsByConnection(@PathVariable Integer connectionId) {
        return userConnectionService.getConnectionsByConnection(connectionId);
    }
}
