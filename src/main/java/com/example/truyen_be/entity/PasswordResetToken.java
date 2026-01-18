package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter @Setter
@NoArgsConstructor
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username; // Lưu username để verify
    
    @Column(nullable = false)
    private String email; // Email nhận OTP
    
    @Column(nullable = false)
    private String otp; // Mã 6 số
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column(nullable = false)
    private int attemptCount = 0; // Đếm số lần nhập sai
    
    public PasswordResetToken(String username, String email, String otp) {
        this.username = username;
        this.email = email; // Email do user tự nhập, không lấy từ DB
        this.otp = otp;
        // OTP hết hạn sau 10 phút
        this.expiryDate = LocalDateTime.now().plusMinutes(10);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public void incrementAttempt() {
        this.attemptCount++;
    }
    
    public boolean isBlocked() {
        return this.attemptCount >= 5; // Block sau 5 lần nhập sai
    }
}