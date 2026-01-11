package com.example.truyen_be.service;

import com.example.truyen_be.dto.LoginRequest;
import com.example.truyen_be.dto.RegisterRequest;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(RegisterRequest request) {
        // ... kiểm tra username tồn tại ...

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // SỬA DÒNG NÀY: Mã hóa mật khẩu trước khi save
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
        user.setAvatarUrl("https://i.pravatar.cc/150?u=" + request.getUsername());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        // Tìm user theo username HOẶC email (Giả sử bạn đã viết phương thức này trong
        // Repository)
        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername())) // Tìm thêm theo email nếu không thấy
                                                                             // username
                .orElseThrow(() -> new RuntimeException("Username hoặc password không đúng"));

        // SỬA TẠI ĐÂY: Dùng passwordEncoder.matches để kiểm tra
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Username hoặc password không đúng");
        }

        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}