package com.example.truyen_be.controller;

import com.example.truyen_be.dto.ChapterSummaryDTO;
import com.example.truyen_be.dto.StoryDetailResponseDTO;
import com.example.truyen_be.dto.StoryResponseDTO;
import com.example.truyen_be.entity.Story;
import com.example.truyen_be.repository.StoryRepository;
import com.example.truyen_be.repository.specification.StorySpecification;
import com.example.truyen_be.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stories")
@CrossOrigin("*")
public class StoryController {

        private final StoryRepository storyRepository;

        @Autowired
        private StoryService storyService;

        StoryController(StoryRepository storyRepository) {
                this.storyRepository = storyRepository;
        }

        // Lấy tất cả truyện có phân trang
        @GetMapping
        public ResponseEntity<Map<String, Object>> getAllStories(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy) {

                Page<Story> storyPage = storyService.getAllStories(page, size, sortBy);

                List<StoryResponseDTO> dtos = storyPage.getContent()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("stories", dtos);
                response.put("currentPage", storyPage.getNumber());
                response.put("totalItems", storyPage.getTotalElements());
                response.put("totalPages", storyPage.getTotalPages());

                return ResponseEntity.ok(response);
        }

        // Lấy truyện hot (DTO)
        @GetMapping("/hot")
        public ResponseEntity<List<StoryResponseDTO>> getHotStories() {
                List<StoryResponseDTO> dtos = storyService.getHotStories()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        // Lấy chi tiết truyện theo ID
        @GetMapping("/{id}")
        public ResponseEntity<StoryDetailResponseDTO> getStoryById(@PathVariable Long id) {
                Story story = storyService.getStoryById(id);

                if (story == null) {
                        return ResponseEntity.notFound().build();
                }

                // Sử dụng hàm convertToDetailDTO mà bạn đã viết sẵn ở cuối file
                return ResponseEntity.ok(convertToDetailDTO(story));
        }

        // Tìm kiếm truyện theo title
        @GetMapping("/search")
        public ResponseEntity<List<StoryResponseDTO>> searchStories(@RequestParam String keyword) {
                List<StoryResponseDTO> dtos = storyService.searchStoriesByTitle(keyword)
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        // Lọc truyện theo thể loại
        @GetMapping("/category/{slug}")
        public ResponseEntity<List<StoryResponseDTO>> getStoriesByCategory(@PathVariable String slug) {
                List<StoryResponseDTO> dtos = storyService.getStoriesByCategory(slug)
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        // StoryController.java - Sửa lại endpoint
        @GetMapping("/advanced-search")
        public ResponseEntity<Map<String, Object>> advancedSearch(
                        @RequestParam(required = false) List<String> include,
                        @RequestParam(required = false) List<String> exclude,
                        @RequestParam(required = false) Integer minChapters,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "updatedAt") String sort,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {

                Specification<Story> spec = StorySpecification.filterStories(
                                include, exclude, minChapters, status, sort);

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
                Page<Story> storyPage = storyRepository.findAll(spec, pageable);

                List<StoryResponseDTO> dtos = storyPage.getContent()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("stories", dtos);
                response.put("currentPage", storyPage.getNumber());
                response.put("totalItems", storyPage.getTotalElements());
                response.put("totalPages", storyPage.getTotalPages());

                return ResponseEntity.ok(response);
        }

        @GetMapping("/top")
        public ResponseEntity<Map<String, Object>> getTopStories(
                        @RequestParam(defaultValue = "day") String period,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {

                LocalDateTime fromDate;
                LocalDateTime now = LocalDateTime.now();

                switch (period) {
                        case "week":
                                fromDate = now.minusWeeks(1);
                                break;
                        case "month":
                                fromDate = now.minusMonths(1);
                                break;
                        default: // day
                                fromDate = now.minusDays(1);
                                break;
                }

                Page<Story> storyPage = storyService.getTopStoriesByPeriod(fromDate, page, size);

                List<StoryResponseDTO> dtos = storyPage.getContent()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("stories", dtos);
                response.put("currentPage", storyPage.getNumber());
                response.put("totalItems", storyPage.getTotalElements());
                response.put("totalPages", storyPage.getTotalPages());

                return ResponseEntity.ok(response);
        }

        // Convert Entity -> DTO (danh sách)
        // ✅ FIX: Thêm rating vào DTO
        private StoryResponseDTO convertToDTO(Story story) {
                StoryResponseDTO dto = new StoryResponseDTO();
                dto.setId(story.getId());
                dto.setTitle(story.getTitle());
                dto.setSlug(story.getSlug());
                dto.setThumbnailUrl(story.getThumbnailUrl());
                dto.setAuthorName(story.getAuthor() != null ? story.getAuthor().getName() : null);
                dto.setCategoryNames(story.getCategories() != null
                                ? story.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet())
                                : null);
                dto.setViews(story.getViews());
                dto.setRating(story.getRating()); // ← THÊM DÒNG NÀY
                dto.setChaptersCount(story.getChaptersCount());
                return dto;
        }

        // Convert Entity -> DetailDTO (chi tiết đầy đủ)
        private StoryDetailResponseDTO convertToDetailDTO(Story story) {
                StoryDetailResponseDTO dto = new StoryDetailResponseDTO();
                dto.setId(story.getId());
                dto.setTitle(story.getTitle());
                dto.setSlug(story.getSlug());
                dto.setThumbnailUrl(story.getThumbnailUrl());
                dto.setDescription(story.getDescription());
                dto.setStatus(story.getStatus());
                dto.setViews(story.getViews());
                dto.setRating(story.getRating());
                dto.setChaptersCount(story.getChaptersCount());

                // Thông tin tác giả
                if (story.getAuthor() != null) {
                        dto.setAuthorId(story.getAuthor().getId());
                        dto.setAuthorName(story.getAuthor().getName());
                        dto.setAuthorAvatarUrl(story.getAuthor().getAvatarUrl());
                }

                // Thể loại
                dto.setCategoryNames(story.getCategories() != null
                                ? story.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet())
                                : null);

                // Danh sách chapter
                dto.setChapters(story.getChapters() != null
                                ? story.getChapters().stream()
                                                .map(c -> new ChapterSummaryDTO(
                                                                c.getId(),
                                                                c.getChapterNumber(),
                                                                c.getTitle(),
                                                                c.getViews(),
                                                                c.getUpdatedAt(),
                                                                c.isVip(),
                                                                c.getPrice()
                                                        ))
                                                                
                                                .collect(Collectors.toList())
                                : null);

                dto.setCreatedAt(story.getCreatedAt());
                dto.setUpdatedAt(story.getUpdatedAt());

                return dto;
        }
}