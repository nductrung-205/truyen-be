package com.example.truyen_be.service;

import com.example.truyen_be.entity.Review;
import com.example.truyen_be.entity.Story;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.ReviewRepository;
import com.example.truyen_be.repository.StoryRepository;
import com.example.truyen_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addReview(Long storyId, String username, Integer rating, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

       
        Review review = new Review();
        review.setUser(user);
        review.setStory(story);
        review.setRating(rating);
        review.setContent(content);
        reviewRepository.save(review);

        updateStoryRating(story);
    }

    private void updateStoryRating(Story story) {
        List<Review> reviews = reviewRepository.findByStoryIdOrderByCreatedAtDesc(story.getId());
        if (reviews.isEmpty())
            return;

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // Làm tròn 1 chữ số thập phân
        story.setRating(Math.round(average * 10.0) / 10.0);
        storyRepository.save(story);
    }
}