package com.example.truyen_be.controller;

import com.example.truyen_be.dto.LoginRequest;
import com.example.truyen_be.dto.RegisterRequest;
import com.example.truyen_be.dto.AuthResponse;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                "Registration successful"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                "Login successful"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy thông tin user
    @GetMapping("/me/{userId}")
    public ResponseEntity<?> getCurrentUser(@PathVariable Long userId) {
        try {
            User user = authService.getUserById(userId);
            AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}