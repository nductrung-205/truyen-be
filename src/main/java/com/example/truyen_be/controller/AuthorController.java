package com.example.truyen_be.controller;

import com.example.truyen_be.dto.AuthorDTO;
import com.example.truyen_be.dto.AuthorDetailDTO;
import com.example.truyen_be.dto.StoryResponseDTO;
import com.example.truyen_be.entity.Author;
import com.example.truyen_be.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin("*")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Lấy danh sách tác giả
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> dtos = authorService.getAllAuthors()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Lấy chi tiết tác giả và danh sách truyện
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDetailDTO> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        return author != null
                ? ResponseEntity.ok(convertToDetailDTO(author))
                : ResponseEntity.notFound().build();
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBio(author.getBio());
        dto.setAvatarUrl(author.getAvatarUrl());
        dto.setCreatedAt(author.getCreatedAt());
        dto.setStoryCount(author.getStories() != null ? author.getStories().size() : 0);
        return dto;
    }

    private AuthorDetailDTO convertToDetailDTO(Author author) {
        AuthorDetailDTO dto = new AuthorDetailDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBio(author.getBio());
        dto.setAvatarUrl(author.getAvatarUrl());
        dto.setCreatedAt(author.getCreatedAt());
        
        // Chuyển danh sách truyện sang DTO
        dto.setStories(author.getStories() != null
                ? author.getStories().stream()
                    .map(story -> {
                        StoryResponseDTO storyDTO = new StoryResponseDTO();
                        storyDTO.setId(story.getId());
                        storyDTO.setTitle(story.getTitle());
                        storyDTO.setSlug(story.getSlug());
                        storyDTO.setThumbnailUrl(story.getThumbnailUrl());
                        storyDTO.setViews(story.getViews());
                        storyDTO.setChaptersCount(story.getChaptersCount());
                        storyDTO.setAuthorName(author.getName());
                        storyDTO.setCategoryNames(story.getCategories() != null
                                ? story.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet())
                                : null);
                        return storyDTO;
                    })
                    .collect(Collectors.toList())
                : null);
        
        return dto;
    }
}