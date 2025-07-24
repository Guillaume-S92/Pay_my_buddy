package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/friends")
public class FriendWebController {

    private final UserService userService;
    private final UserConnectionService userConnectionService;

    @Autowired
    public FriendWebController(UserService userService, UserConnectionService userConnectionService) {
        this.userService = userService;
        this.userConnectionService = userConnectionService;
    }

    @GetMapping
    public String friendPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "friends";
    }

    @GetMapping("/add")
    public String showAddFriendForm(Model model) {
        model.addAttribute("email", "");
        model.addAttribute("msg", "");
        model.addAttribute("error", "");
        return "add-friend";
    }

    @PostMapping("/add")
    public String addFriend(@RequestParam String userId, @RequestParam String email, Model model) {
        // userId = l’ID de l’utilisateur courant (pour le prototype, tu peux le forcer à 1)
        User user = userService.getUserById(Integer.parseInt(userId)).orElse(null);
        User friend = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé.");
            return "add-friend";
        }
        if (friend == null) {
            model.addAttribute("error", "Aucun utilisateur trouvé avec cet email.");
            return "add-friend";
        }
        if (user.getId().equals(friend.getId())) {
            model.addAttribute("error", "Impossible de s’ajouter soi-même.");
            return "add-friend";
        }

        // Créer la connexion d’amitié
        userConnectionService.createConnection(new UserConnection(user, friend));
        model.addAttribute("msg", "Ami ajouté !");
        return "add-friend";
    }
}
