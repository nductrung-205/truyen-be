package com.example.truyen_be.dto;

import lombok.*;
import java.time.LocalDateTime;

// DTO cho danh sách chapter (không có content)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO {
    private Long id;
    private Integer chapterNumber;
    private String title;
    private Long views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

