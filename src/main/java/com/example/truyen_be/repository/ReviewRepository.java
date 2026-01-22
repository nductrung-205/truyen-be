package com.example.truyen_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.truyen_be.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStoryIdOrderByCreatedAtDesc(Long storyId);
    boolean existsByUserIdAndStoryId(Long userId, Long storyId);
    List<Review> findByUserUsernameOrderByCreatedAtDesc(String username); 
}
