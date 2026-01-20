package com.example.truyen_be.service;

import com.example.truyen_be.dto.LoginRequest;
import com.example.truyen_be.dto.RegisterRequest;
import com.example.truyen_be.entity.PasswordResetToken;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.PasswordResetTokenRepository;
import com.example.truyen_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    // ============ ĐĂNG KÝ (2 BƯỚC) ============

    /**
     * BƯỚC 1: Gửi OTP để đăng ký (Dành cho Email chưa tồn tại)
     */
    @Transactional
    public void sendOtpForRegistration(String email) {
        // Kiểm tra xem email đã có tài khoản chưa
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email này đã được đăng ký tài khoản!");
        }

        // Xóa mã cũ của email này (nếu có)
        tokenRepository.deleteByUsername(email); 

        String otp = generateOtp();
        // Lưu vào bảng Token, dùng email làm định danh tạm thời (thay cho username)
        PasswordResetToken token = new PasswordResetToken(email, email, otp);
        tokenRepository.save(token);

        // Gửi email
        emailService.sendOtpEmail(email, otp, "Người dùng mới");
    }

    /**
     * BƯỚC 2: Xác thực OTP và Tạo User
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 1. Kiểm tra mã OTP
        PasswordResetToken token = tokenRepository.findByUsernameAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("Mã xác thực không chính xác"));

        if (token.isExpired()) throw new RuntimeException("Mã xác thực đã hết hạn");

        // 2. Tạo User
        User user = new User();
        // Tự tạo username từ email
        user.setUsername(request.getEmail().split("@")[0] + "_" + random.nextInt(1000));
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setAvatarUrl("https://i.pravatar.cc/150?u=" + request.getEmail());

        // 3. Xóa token sau khi dùng
        tokenRepository.delete(token);

        return userRepository.save(user);
    }

    // ============ ĐĂNG NHẬP ============

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        return user;
    }

    // ============ QUÊN MẬT KHẨU ============

    /**
     * Gửi OTP Quên mật khẩu (Dành cho Email ĐÃ tồn tại)
     */
    @Transactional
    public void sendOtpForgotPassword(String email) {
        // Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa được đăng ký trong hệ thống"));

        tokenRepository.deleteByUsername(user.getUsername());

        String otp = generateOtp();
        PasswordResetToken token = new PasswordResetToken(user.getUsername(), email, otp);
        tokenRepository.save(token);

        emailService.sendOtpEmail(email, otp, user.getUsername());
    }

    @Transactional
    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        // Tìm user từ email trước để lấy username
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        PasswordResetToken resetToken = tokenRepository.findByUsernameAndOtp(user.getUsername(), otp)
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác"));

        if (resetToken.isExpired()) throw new RuntimeException("Mã OTP đã hết hạn");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    private String generateOtp() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
    
    public long getOtpRemainingTime(String email) {
        return tokenRepository.findByUsername(email)
                .map(t -> Math.max(0, java.time.Duration.between(java.time.LocalDateTime.now(), t.getExpiryDate()).getSeconds()))
                .orElse(0L);
    }
}