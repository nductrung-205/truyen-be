package com.example.truyen_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
    private Integer coins;
    private Integer exp;
    private Integer checkInStreak;
    private String lastCheckIn; // Dạng String để dễ parse ở frontend
}