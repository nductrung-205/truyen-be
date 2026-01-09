package com.example.truyen_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.truyen_be.entity.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // Lấy danh sách chương của 1 truyện
    List<Chapter> findByStoryIdOrderByChapterNumberAsc(Long storyId);
    
    // Tìm 1 chương cụ thể của truyện
    Optional<Chapter> findByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber);
}