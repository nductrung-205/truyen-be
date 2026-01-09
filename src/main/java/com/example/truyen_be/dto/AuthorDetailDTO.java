package com.example.truyen_be.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDetailDTO {
    private Long id;
    private String name;
    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private List<StoryResponseDTO> stories;
}
