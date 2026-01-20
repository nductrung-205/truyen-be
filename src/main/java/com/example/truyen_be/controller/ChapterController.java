package com.example.truyen_be.controller;

import com.example.truyen_be.dto.ChapterDTO;
import com.example.truyen_be.dto.ChapterDetailDTO;
import com.example.truyen_be.entity.Chapter;
import com.example.truyen_be.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stories/{storyId}/chapters")
@CrossOrigin("*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    // Lấy danh sách chapter của 1 truyện
    @GetMapping
    public ResponseEntity<List<ChapterDTO>> getChaptersByStory(@PathVariable Long storyId) {
        List<ChapterDTO> dtos = chapterService.getChaptersByStory(storyId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{chapterNumber}")
    public ResponseEntity<ChapterDetailDTO> getChapterContent(
            @PathVariable Long storyId,
            @PathVariable Integer chapterNumber) {

        Chapter chapter = chapterService.increaseChapterView(storyId, chapterNumber);

        if (chapter == null) {
            return ResponseEntity.notFound().build();
        }

        List<Chapter> allChapters = chapterService.getChaptersByStory(storyId);

        ChapterDetailDTO dto = convertToDetailDTO(chapter, allChapters);
        return ResponseEntity.ok(dto);
    }

    // Convert sang DTO (danh sách)
    private ChapterDTO convertToDTO(Chapter chapter) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(chapter.getId());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setTitle(chapter.getTitle());
        dto.setViews(chapter.getViews());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        return dto;
    }

    // Convert sang DetailDTO (có content + navigation)
    private ChapterDetailDTO convertToDetailDTO(Chapter chapter, List<Chapter> allChapters) {
        ChapterDetailDTO dto = new ChapterDetailDTO();
        dto.setId(chapter.getId());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setTitle(chapter.getTitle());
        dto.setViews(chapter.getViews());
        dto.setContent(chapter.getContent() != null ? chapter.getContent().getContent() : null);
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());

        // Tìm chapter trước/sau
        int currentIndex = -1;
        for (int i = 0; i < allChapters.size(); i++) {
            if (allChapters.get(i).getId().equals(chapter.getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex > 0) {
            dto.setPreviousChapter(allChapters.get(currentIndex - 1).getChapterNumber());
        }
        if (currentIndex >= 0 && currentIndex < allChapters.size() - 1) {
            dto.setNextChapter(allChapters.get(currentIndex + 1).getChapterNumber());
        }

        return dto;
    }
}