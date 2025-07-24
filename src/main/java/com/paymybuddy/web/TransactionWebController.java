package com.paymybuddy.web;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionWebController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions";
    }

    @GetMapping("/new")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("users", userService.getAllUsers());
        return "transaction-form";
    }

    @PostMapping
    public String createTransaction(@ModelAttribute Transaction transaction, @RequestParam Integer senderId, @RequestParam Integer receiverId) {
        User sender = userService.getUserById(senderId).orElseThrow();
        User receiver = userService.getUserById(receiverId).orElseThrow();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }
}
