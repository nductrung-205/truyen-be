package com.example.truyen_be.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterSummaryDTO {
    private Long id;
    private Integer chapterNumber;
    private String title;
    private Long views;
    private LocalDateTime updatedAt;
}
