package com.example.truyen_be.dto;

import lombok.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private Integer storyCount; 
}