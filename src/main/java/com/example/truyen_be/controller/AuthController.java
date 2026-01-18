package com.example.truyen_be.controller;

import com.example.truyen_be.dto.*;
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

    // ============ CÁC API CŨ ============

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
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

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
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

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

    // ============ API QUÊN MẬT KHẨU (OTP) ============

    /**
     * Bước 1: Gửi OTP qua email
     * POST /api/auth/send-otp
     * Body: { 
     *   "username": "john123",
     *   "email": "john@example.com" 
     * }
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest request) {
        try {
            authService.sendOtp(request.getUsername(), request.getEmail());
            return ResponseEntity.ok(new MessageResponse(
                "Mã OTP đã được gửi đến email " + maskEmail(request.getEmail()) + 
                ". Mã có hiệu lực trong 10 phút.",
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * Bước 2: Xác thực OTP và đặt lại mật khẩu
     * POST /api/auth/verify-otp-reset
     * Body: { 
     *   "username": "john123",
     *   "otp": "123456",
     *   "newPassword": "newpass123"
     * }
     */
    @PostMapping("/verify-otp-reset")
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestBody VerifyOtpAndResetRequest request) {
        try {
            authService.verifyOtpAndResetPassword(
                request.getUsername(), 
                request.getOtp(), 
                request.getNewPassword()
            );
            return ResponseEntity.ok(new MessageResponse(
                "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập lại.",
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * (Optional) Kiểm tra thời gian còn lại của OTP
     * GET /api/auth/otp-remaining-time?username=john123
     */
    @GetMapping("/otp-remaining-time")
    public ResponseEntity<?> getOtpRemainingTime(@RequestParam String username) {
        try {
            long seconds = authService.getOtpRemainingTime(username);
            return ResponseEntity.ok(new OtpTimeResponse(seconds, seconds > 0));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    // Helper: Ẩn bớt email để bảo mật (john@gmail.com -> j***@gmail.com)
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String username = parts[0];
        if (username.length() <= 2) return email;
        return username.charAt(0) + "***@" + parts[1];
    }
}

// DTO cho response thời gian OTP
class OtpTimeResponse {
    public long remainingSeconds;
    public boolean isValid;
    
    public OtpTimeResponse(long remainingSeconds, boolean isValid) {
        this.remainingSeconds = remainingSeconds;
        this.isValid = isValid;
    }
}