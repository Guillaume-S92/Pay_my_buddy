package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Affiche la page de login
    @GetMapping("/login")
    public String loginForm(Model model, @RequestParam(required = false) String error) {
        if (error != null) model.addAttribute("error", "Identifiants incorrects");
        return "login";
    }

    // Affiche la page de création de compte
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Enregistre un utilisateur
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Email déjà utilisé !");
            return "register";
        }
        userService.createUser(user);
        model.addAttribute("msg", "Compte créé ! Connectez-vous.");
        return "redirect:/login";
    }
}
