package com.example.truyen_be.dto;

import lombok.Data;
import java.util.Set;

@Data
public class StoryResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private String authorName;
    private Set<String> categoryNames;
    private Long views;
    private Integer chaptersCount;
}