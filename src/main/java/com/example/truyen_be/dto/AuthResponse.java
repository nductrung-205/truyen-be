package com.example.truyen_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
    private String message;
}
