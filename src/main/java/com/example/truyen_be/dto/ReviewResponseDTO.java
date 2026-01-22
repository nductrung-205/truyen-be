package com.example.truyen_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private Integer userExp; // Để hiển thị Level/Cấp bậc như trong hình
}