package com.example.demo.controller.mvc;

import com.example.demo.model.AccountType;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AdminAccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("users", userService.findAll());
        return "admin/accounts";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long userId,
                         @RequestParam AccountType accountType,
                         RedirectAttributes redirectAttrs) {
        accountService.createForUser(userId, accountType);
        redirectAttrs.addFlashAttribute("success",
                "Счёт типа «" + accountType.getDisplayName() + "» создан");
        return "redirect:/admin/accounts";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        accountService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Счёт удалён");
        return "redirect:/admin/accounts";
    }
}
