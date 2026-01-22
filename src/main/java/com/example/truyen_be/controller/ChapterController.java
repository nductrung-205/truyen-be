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
            @PathVariable Integer chapterNumber,
            @RequestParam(required = false) Long userId) {

        // 1. Lấy thông tin chapter hiện tại
        Chapter chapter = chapterService.getChapterByStoryAndNumber(storyId, chapterNumber);

        if (chapter == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Lấy danh sách tất cả chapter của truyện này (để phục vụ việc tính toán
        // Next/Prev trong DTO)
        List<Chapter> allChapters = chapterService.getChaptersByStory(storyId);

        // 3. Kiểm tra xem đã mở khóa chưa
        boolean unlocked = chapterService.isChapterUnlocked(userId, chapter.getId());

        // 4. Convert sang DTO (Bây giờ đã đủ 2 tham số: chapter và allChapters)
        ChapterDetailDTO dto = convertToDetailDTO(chapter, allChapters);

        // 5. Xử lý ẩn nội dung nếu chưa mở khóa
        if (!unlocked) {
            dto.setContent(null); // Giấu nội dung thực
            dto.setLocked(true); // Đánh dấu là bị khóa
        } else {
            dto.setLocked(false);
            // Chỉ tăng view khi người dùng thực sự đọc được nội dung
            chapterService.increaseChapterView(storyId, chapterNumber);
        }

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{chapterId}/unlock")
    public ResponseEntity<?> unlock(@PathVariable Long chapterId, @RequestParam Long userId) {
        chapterService.unlockChapter(userId, chapterId);
        return ResponseEntity.ok("Mở khóa thành công");
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