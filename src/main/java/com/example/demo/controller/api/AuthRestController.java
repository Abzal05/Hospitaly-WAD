package com.example.demo.controller.api;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthRestController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication.getName());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Проверяем что логин не пустой
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Логин не может быть пустым\"}");
        }

        // Проверяем что пароль не пустой
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Пароль не может быть пустым\"}");
        }

        // Проверяем длину логина
        if (request.getUsername().length() < 3) {
            return ResponseEntity.badRequest().body("{\"error\":\"Логин должен быть минимум 3 символа\"}");
        }

        // Проверяем длину пароля
        if (request.getPassword().length() < 4) {
            return ResponseEntity.badRequest().body("{\"error\":\"Пароль должен быть минимум 4 символа\"}");
        }

        // Проверяем что пользователь не существует
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("{\"error\":\"Этот логин уже используется\"}");
        }

        try {
            // Создаём нового пользователя
            User u = new User();
            u.setUsername(request.getUsername());
            u.setPassword(passwordEncoder.encode(request.getPassword()));
            u.setEnabled(true);

            // Присваиваем роль USER
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
            u.setRoles(roles);

            // Сохраняем пользователя
            userRepository.save(u);

            return ResponseEntity.ok("{\"message\":\"Аккаунт успешно создан! Вы можете войти с новыми учетными данными\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Ошибка при создании аккаунта: " + e.getMessage() + "\"}");
        }
    }
}
