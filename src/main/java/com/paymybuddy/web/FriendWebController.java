package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/friends")
public class FriendWebController {

    private final UserService userService;
    private final UserConnectionService userConnectionService;
    private final AuthUtil authUtil;

    public FriendWebController(UserService userService, UserConnectionService userConnectionService, AuthUtil authUtil) {
        this.userService = userService;
        this.userConnectionService = userConnectionService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public String friendPage(Model model) {
        User currentUser = authUtil.getCurrentUser();
        model.addAttribute("friends", userConnectionService.getConnectionsByUser(currentUser.getId()));
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
    public String addFriend(@RequestParam String email, Model model) {
        User currentUser = authUtil.getCurrentUser();
        User friend = userService.getUserByEmail(email).orElse(null);

        if (friend == null) {
            model.addAttribute("error", "Aucun utilisateur trouvé avec cet email.");
            return "add-friend";
        }
        if (currentUser.getId().equals(friend.getId())) {
            model.addAttribute("error", "Impossible de s’ajouter soi-même.");
            return "add-friend";
        }
        // Optionnel : vérifie si la connexion existe déjà !
        userConnectionService.createConnection(new UserConnection(currentUser, friend));
        model.addAttribute("msg", "Ami ajouté !");
        return "add-friend";
    }
}
