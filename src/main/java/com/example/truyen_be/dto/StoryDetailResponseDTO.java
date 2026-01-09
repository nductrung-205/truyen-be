package com.example.truyen_be.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryDetailResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private String description;
    private String status;
    private Long views;
    private Double rating;
    private Integer chaptersCount;
    
    // Thông tin tác giả
    private Long authorId;
    private String authorName;
    private String authorAvatarUrl;
    
    // Danh sách thể loại
    private Set<String> categoryNames;
    
    // Danh sách chapter (tóm tắt)
    private List<ChapterSummaryDTO> chapters;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
