package com.example.truyen_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpAndResetRequest {
    private String username;
    private String otp;
    private String newPassword;
}
