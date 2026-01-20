package com.example.truyen_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.truyen_be.entity.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByStory_IdOrderByChapterNumberAsc(Long storyId);

    Optional<Chapter> findByStory_IdAndChapterNumber(Long storyId, Integer chapterNumber);
}
