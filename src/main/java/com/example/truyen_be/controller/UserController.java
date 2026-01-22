package com.example.truyen_be.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.truyen_be.service.UserService;
import com.example.truyen_be.dto.UserDTO;

@RestController
@RequestMapping("/api/users") // ✅ THÊM BASE PATH
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/check-in/{userId}")
    public ResponseEntity<UserDTO> checkIn(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.performCheckIn(userId));
    }

    // ✅ THÊM ENDPOINT LẤY THÔNG TIN USER
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}