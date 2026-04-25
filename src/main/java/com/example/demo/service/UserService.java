package com.example.demo.service;

import com.example.demo.dto.UserEditDto;
import com.example.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User create(UserEditDto dto);
    User update(Long id, UserEditDto dto);
    void delete(Long id);
    void toggleEnabled(Long id);
}
