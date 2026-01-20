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

    // ================== ĐĂNG KÝ (2 BƯỚC) ==================

    /**
     * Bước 1: Gửi OTP để đăng ký tài khoản mới
     * POST /api/auth/register/send-otp
     */
    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody SendOtpRequest request) {
        try {
            authService.sendOtpForRegistration(request.getEmail());
            return ResponseEntity.ok(new MessageResponse(
                    "Mã OTP đăng ký đã được gửi đến " + maskEmail(request.getEmail()),
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * Bước 2: Xác thực OTP và hoàn tất đăng ký
     * POST /api/auth/register
     */
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
                    "Đăng ký tài khoản thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    // ================== ĐĂNG NHẬP ==================

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
                    "Đăng nhập thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    // ================== QUÊN MẬT KHẨU ==================

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest request) {
        try {
            // Mặc định gọi luồng gửi OTP đăng ký
            authService.sendOtpForRegistration(request.getEmail());
            return ResponseEntity.ok(new MessageResponse(
                    "Mã OTP đã được gửi đến " + maskEmail(request.getEmail()),
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * Bước 1: Gửi OTP cho người dùng quên mật khẩu
     * POST /api/auth/forgot-password/send-otp
     */
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(@RequestBody SendOtpRequest request) {
        try {
            authService.sendOtpForgotPassword(request.getEmail());
            return ResponseEntity.ok(new MessageResponse(
                    "Mã OTP khôi phục mật khẩu đã được gửi đến " + maskEmail(request.getEmail()),
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * Bước 2: Xác thực OTP và đặt lại mật khẩu mới
     * POST /api/auth/forgot-password/reset
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody VerifyOtpAndResetRequest request) {
        try {
            authService.verifyOtpAndResetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse(
                    "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập lại.",
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    // ================== TIỆN ÍCH ==================

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
                    null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Kiểm tra thời gian còn lại của OTP (Sử dụng Email làm định danh)
     */
    @GetMapping("/otp-remaining-time")
    public ResponseEntity<?> getOtpRemainingTime(@RequestParam String email) {
        try {
            long seconds = authService.getOtpRemainingTime(email);
            return ResponseEntity.ok(new OtpTimeResponse(seconds, seconds > 0));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;
        String[] parts = email.split("@");
        String username = parts[0];
        if (username.length() <= 2)
            return email;
        return username.charAt(0) + "***@" + parts[1];
    }
}

// Các class bổ trợ để đồng bộ dữ liệu
class OtpTimeResponse {
    public long remainingSeconds;
    public boolean isValid;

    public OtpTimeResponse(long remainingSeconds, boolean isValid) {
        this.remainingSeconds = remainingSeconds;
        this.isValid = isValid;
    }
}