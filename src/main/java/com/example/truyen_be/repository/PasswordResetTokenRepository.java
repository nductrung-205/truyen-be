package com.example.truyen_be.repository;

import com.example.truyen_be.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);
    Optional<PasswordResetToken> findByEmail(String email);
    Optional<PasswordResetToken> findByUsername(String username);
    Optional<PasswordResetToken> findByUsernameAndOtp(String username, String otp);
    
    // --- THÊM PHƯƠNG THỨC NÀY ---
    @Modifying
    @Transactional
    void deleteByUsername(String username);
    // ----------------------------

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.email = ?1")
    void deleteByEmail(String email);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1")
    void deleteExpiredTokens(LocalDateTime now);
}