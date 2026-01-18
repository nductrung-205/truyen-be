package com.example.truyen_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequest {
    private String username; // Username để kiểm tra trong DB
    private String email;    // Email để nhận OTP (không cần trùng DB)
}
