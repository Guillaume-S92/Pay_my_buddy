package com.paymybuddy.web;

import com.paymybuddy.model.User;
import com.paymybuddy.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AuthUtil authUtil;

    public HomeController(AuthUtil authUtil) { this.authUtil = authUtil; }

    @GetMapping("/")
    public String home(Model model) {
        User user = null;
        try {
            user = authUtil.getCurrentUser();
        } catch (Exception e) { /* ignoré si pas connecté */ }
        model.addAttribute("user", user);
        return "home";
    }
}
