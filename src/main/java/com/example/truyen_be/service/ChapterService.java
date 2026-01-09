package com.example.truyen_be.service;

import com.example.truyen_be.entity.Chapter;
import com.example.truyen_be.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChapterService {
    
    @Autowired
    private ChapterRepository chapterRepository;

    // Lấy danh sách chapter của 1 truyện
    public List<Chapter> getChaptersByStory(Long storyId) {
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId);
    }

    // Lấy nội dung chapter cụ thể
    public Chapter getChapterByStoryAndNumber(Long storyId, Integer chapterNumber) {
        return chapterRepository.findByStoryIdAndChapterNumber(storyId, chapterNumber)
                .orElse(null);
    }

    // Tăng view count khi đọc chapter
    @Transactional
    public Chapter increaseChapterView(Long storyId, Integer chapterNumber) {
        Chapter chapter = getChapterByStoryAndNumber(storyId, chapterNumber);
        if (chapter != null) {
            chapter.setViews(chapter.getViews() + 1);
            return chapterRepository.save(chapter);
        }
        return null;
    }
}