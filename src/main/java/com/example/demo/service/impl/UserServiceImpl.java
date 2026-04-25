package com.example.demo.service.impl;

import com.example.demo.dto.UserEditDto;
import com.example.demo.event.UserRegisteredEvent;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SystemStatsService;
import com.example.demo.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;   // Observer
    private final SystemStatsService statsService;            // Singleton

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           ApplicationEventPublisher eventPublisher,
                           SystemStatsService statsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.statsService = statsService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: id=" + id));
    }

    @Override
    public User create(UserEditDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Логин уже занят: " + dto.getUsername());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("Пароль обязателен при создании пользователя");
        }

        User saved = userRepository.save(User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(dto.isEnabled())
                .roles(resolveRoles(dto.getRoleNames()))
                .build());

        // Observer: publish domain event (DomainEventListener reacts asynchronously)
        eventPublisher.publishEvent(new UserRegisteredEvent(this, saved));

        // Singleton: update shared runtime counter
        statsService.incrementUsers();

        return saved;
    }

    @Override
    public User update(Long id, UserEditDto dto) {
        User user = findById(id);

        if (!user.getUsername().equals(dto.getUsername())
                && userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Логин уже занят: " + dto.getUsername());
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.isEnabled());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRoleNames() != null && !dto.getRoleNames().isEmpty()) {
            user.setRoles(resolveRoles(dto.getRoleNames()));
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    @Override
    public void toggleEnabled(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Роль ROLE_USER не найдена")));
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Роль не найдена: " + name)))
                .collect(Collectors.toSet());
    }
}
