package com.example.truyen_be.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyReviewResponseDTO {
    private Long id;
    private Long storyId;
    private String storyTitle;
    private String storyThumbnail;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}