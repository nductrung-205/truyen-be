package com.example.truyen_be.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.UserRepository;
import com.example.truyen_be.dto.UserDTO;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ THÊM METHOD NÀY ĐỂ CONVERT
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole());
        dto.setCoins(user.getCoins());
        dto.setExp(user.getExp());
        dto.setCheckInStreak(user.getCheckInStreak());

        if (user.getLastCheckIn() != null) {
            dto.setLastCheckIn(user.getLastCheckIn().toString());
        }

        return dto;
    }

    public UserDTO performCheckIn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));

        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra xem hôm nay đã điểm danh chưa
        if (user.getLastCheckIn() != null && user.getLastCheckIn().toLocalDate().equals(now.toLocalDate())) {
            throw new RuntimeException("Hôm nay bạn đã điểm danh rồi!");
        }

        // 2. Tính toán streak
        int currentStreak = 1;
        // Kiểm tra null cho checkInStreak
        int oldStreak = (user.getCheckInStreak() == null) ? 0 : user.getCheckInStreak();

        if (user.getLastCheckIn() != null
                && user.getLastCheckIn().toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            currentStreak = oldStreak + 1;
        }
        user.setCheckInStreak(currentStreak);

        // 3. Tính toán số xu thưởng
        int rewardCoins = 10;
        if (currentStreak % 7 == 0) {
            rewardCoins += 100;
        }

        // 4. CẬP NHẬT USER (Xử lý chống Null ở đây)
        int currentCoins = (user.getCoins() == null) ? 0 : user.getCoins();
        int currentExp = (user.getExp() == null) ? 0 : user.getExp();

        user.setCoins(currentCoins + rewardCoins);
        user.setExp(currentExp + 20); // Tăng exp
        user.setLastCheckIn(now);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // ✅ THÊM METHOD LẤY THÔNG TIN USER
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));
        return convertToDTO(user);
    }
}