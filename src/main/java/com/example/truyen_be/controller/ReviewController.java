package com.example.truyen_be.controller;

import com.example.truyen_be.dto.MyReviewResponseDTO;
import com.example.truyen_be.dto.ReviewRequest;
import com.example.truyen_be.dto.ReviewResponseDTO;
import com.example.truyen_be.entity.Review;
import com.example.truyen_be.service.ReviewService;
import com.example.truyen_be.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private ReviewRepository reviewRepository;

    // ✅ Sửa: Đọc username từ header thay vì Principal
    @PostMapping("/{storyId}")
    public ResponseEntity<?> postReview(
            @PathVariable Long storyId, 
            @RequestBody ReviewRequest req, 
            @RequestHeader(value = "X-User", required = false) String username) {
        
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập");
        }

        try {
            reviewService.addReview(storyId, username, req.getRating(), req.getContent());
            return ResponseEntity.ok("Đánh giá thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Long storyId) {
        List<Review> reviews = reviewRepository.findByStoryIdOrderByCreatedAtDesc(storyId);
        
        List<ReviewResponseDTO> dtos = reviews.stream().map(r -> new ReviewResponseDTO(
                r.getId(),
                r.getUser().getUsername(),
                r.getUser().getAvatarUrl(),
                r.getRating(),
                r.getContent(),
                r.getCreatedAt(),
                r.getUser().getExp()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<MyReviewResponseDTO>> getMyReviews(@PathVariable String username) {
        List<Review> reviews = reviewRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        
        List<MyReviewResponseDTO> dtos = reviews.stream().map(r -> new MyReviewResponseDTO(
                r.getId(),
                r.getStory().getId(),
                r.getStory().getTitle(),
                r.getStory().getThumbnailUrl(),
                r.getRating(),
                r.getContent(),
                r.getCreatedAt()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}