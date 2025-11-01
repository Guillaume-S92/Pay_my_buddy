package com.paymybuddy.web;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final UserConnectionService userConnectionService;
    private final AuthUtil authUtil;

    public TransactionWebController(TransactionService transactionService,
                                    UserService userService,
                                    UserConnectionService userConnectionService,
                                    AuthUtil authUtil) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.userConnectionService = userConnectionService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public String listTransactions(Model model) {
        User currentUser = authUtil.getCurrentUser();
        model.addAttribute("transactions",
                transactionService.getTransactionsBySender(currentUser.getId()));
        return "transactions";
    }

    @GetMapping("/new")
    public String showTransactionForm(Model model) {
        User currentUser = authUtil.getCurrentUser();
        model.addAttribute("transaction", new Transaction());

        // Liste uniquement les amis du user connecté
        List<User> friends = userConnectionService.getConnectionsByUser(currentUser.getId())
                .stream()
                .map(UserConnection::getConnection)
                .toList();

        model.addAttribute("users", friends);
        return "transaction-form";
    }

    @PostMapping
    public String createTransaction(@ModelAttribute Transaction transaction, @RequestParam Integer receiverId, Model model) {

        User sender = authUtil.getCurrentUser();
        User receiver = userService.getUserById(receiverId).orElseThrow();

        // Vérifie que le receiver est bien un ami
        boolean isFriend = userConnectionService.getConnectionsByUser(sender.getId())
                .stream()
                .anyMatch(uc -> uc.getConnection().getId().equals(receiverId));

        if (!isFriend) {
            prepareFormModel(model, sender, transaction);
            model.addAttribute("error", "Vous ne pouvez envoyer de l'argent qu'à vos amis.");
            return "transaction-form";
        }


        if (sender.getId().equals(receiver.getId())) {
            prepareFormModel(model, sender, transaction);
            model.addAttribute("error", "Impossible d’envoyer de l’argent à soi-même.");
            return "transaction-form";
        }


        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        try {
            transactionService.createTransaction(transaction);
        } catch (IllegalArgumentException e) {
            // Cas typique : montant nul, montant <= 0, etc.
            prepareFormModel(model, sender, transaction);
            model.addAttribute("error", e.getMessage());
            return "transaction-form";
        }

        // Succès : retour vers la liste des transactions
        return "redirect:/transactions";
    }

    private void prepareFormModel(Model model, User sender, Transaction transaction) {
        model.addAttribute("transaction", transaction);

        List<User> friends = userConnectionService.getConnectionsByUser(sender.getId())
                .stream()
                .map(UserConnection::getConnection)
                .toList();

        model.addAttribute("users", friends);
    }
}
