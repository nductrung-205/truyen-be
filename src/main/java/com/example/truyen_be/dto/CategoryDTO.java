package com.example.truyen_be.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer storyCount; // Số lượng truyện trong thể loại
}