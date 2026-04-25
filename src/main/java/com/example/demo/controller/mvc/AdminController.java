package com.example.demo.controller.mvc;

import com.example.demo.dto.UserEditDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createForm(Model model) {
        model.addAttribute("userDto", new UserEditDto());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/user-create";
    }

    @PostMapping("/users/create")
    public String createSubmit(@Valid @ModelAttribute("userDto") UserEditDto dto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin/user-create";
        }
        userService.create(dto);
        redirectAttrs.addFlashAttribute("success", "Пользователь «" + dto.getUsername() + "» создан");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        UserEditDto dto = new UserEditDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setRoleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        model.addAttribute("userDto", dto);
        model.addAttribute("userId", id);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/user-edit";
    }

    @PostMapping("/users/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("userDto") UserEditDto dto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin/user-edit";
        }
        userService.update(id, dto);
        redirectAttrs.addFlashAttribute("success", "Пользователь обновлён");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Пользователь удалён");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.toggleEnabled(id);
        redirectAttrs.addFlashAttribute("success", "Статус пользователя изменён");
        return "redirect:/admin/users";
    }
}
