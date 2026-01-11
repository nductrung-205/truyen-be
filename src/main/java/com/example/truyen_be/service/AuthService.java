package com.example.truyen_be.service;

import com.example.truyen_be.dto.LoginRequest;
import com.example.truyen_be.dto.RegisterRequest;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    public User register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Check if email exists (optional)
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: Hash password with BCrypt
        user.setRole("USER");
        user.setAvatarUrl("https://i.pravatar.cc/150?u=" + request.getUsername());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Username hoặc password không đúng"));

        // TODO: Verify password with BCrypt
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Username hoặc password không đúng");
        }

        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}