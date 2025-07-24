package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Identifiants incorrects");
        if (logout != null) model.addAttribute("msg", "Déconnexion réussie !");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Email déjà utilisé !");
            return "register";
        }
        userService.createUser(user);
        return "redirect:/login";
    }
}
