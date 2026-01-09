package com.example.truyen_be.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDetailDTO {
    private Long id;
    private Integer chapterNumber;
    private String title;
    private Long views;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Integer previousChapter;
    private Integer nextChapter;
}
