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

    public User register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setAvatarUrl("https://i.pravatar.cc/150?u=" + request.getUsername());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("Username hoặc password không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Username hoặc password không đúng");
        }

        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    // ============ CHỨC NĂNG QUÊN MẬT KHẨU (OTP) ============

    /**
     * Tạo mã OTP 6 số ngẫu nhiên
     */
    private String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // 100000 -> 999999
        return String.valueOf(otp);
    }

    /**
     * Bước 1: Gửi OTP qua email
     * - Kiểm tra username có tồn tại không
     * - Tạo OTP 6 số
     * - Gửi về email mà user nhập (không cần trùng DB)
     */
    @Transactional
    public void sendOtp(String username, String email) {
        // Kiểm tra username có tồn tại trong DB không
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username không tồn tại trong hệ thống"));

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Email không hợp lệ");
        }

        // Xóa các OTP cũ của username này (nếu có)
        tokenRepository.deleteByUsername(username);

        // Tạo OTP mới
        String otp = generateOtp();
        PasswordResetToken resetToken = new PasswordResetToken(username, email, otp);
        tokenRepository.save(resetToken);

        // Gửi OTP qua email
        emailService.sendOtpEmail(email, otp, username);
    }

    /**
     * Bước 2: Xác thực OTP và đặt lại mật khẩu
     * - Kiểm tra username + OTP có khớp không
     * - Kiểm tra OTP còn hạn không
     * - Kiểm tra có bị block không (nhập sai quá 5 lần)
     * - Đặt lại mật khẩu
     */
    @Transactional
    public void verifyOtpAndResetPassword(String username, String otp, String newPassword) {
        // Tìm token
        PasswordResetToken resetToken = tokenRepository.findByUsernameAndOtp(username, otp)
                .orElseThrow(() -> {
                    // Nếu không tìm thấy, tăng attempt count
                    tokenRepository.findByUsername(username).ifPresent(token -> {
                        token.incrementAttempt();
                        tokenRepository.save(token);
                    });
                    return new RuntimeException("Mã OTP không chính xác");
                });

        // Kiểm tra đã bị block chưa
        if (resetToken.isBlocked()) {
            throw new RuntimeException("Bạn đã nhập sai quá 5 lần. Vui lòng yêu cầu mã OTP mới.");
        }

        // Kiểm tra OTP đã hết hạn chưa
        if (resetToken.isExpired()) {
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        // Kiểm tra OTP đã được sử dụng chưa
        if (resetToken.isUsed()) {
            throw new RuntimeException("Mã OTP đã được sử dụng");
        }

        // Validate mật khẩu mới
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Tìm user và cập nhật mật khẩu
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Đánh dấu token đã sử dụng
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Gửi email thông báo (về email trong DB)
        emailService.sendPasswordChangedNotification(user.getEmail(), user.getUsername());
    }

    /**
     * Kiểm tra còn bao nhiêu thời gian OTP hết hạn (optional - cho UX tốt hơn)
     */
    public long getOtpRemainingTime(String username) {
        return tokenRepository.findByUsername(username)
                .map(token -> {
                    long seconds = java.time.Duration.between(
                        java.time.LocalDateTime.now(), 
                        token.getExpiryDate()
                    ).getSeconds();
                    return Math.max(0, seconds);
                })
                .orElse(0L);
    }
}