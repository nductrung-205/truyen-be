package com.example.truyen_be.service;

import com.example.truyen_be.entity.Chapter;
import com.example.truyen_be.entity.Story;
import com.example.truyen_be.repository.ChapterRepository;
import com.example.truyen_be.repository.StoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChapterService {
  
    @Autowired 
    private StoryRepository storyRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    // Lấy danh sách chapter của 1 truyện
    public List<Chapter> getChaptersByStory(Long storyId) {
        return chapterRepository.findByStory_IdOrderByChapterNumberAsc(storyId);
    }

    // Lấy nội dung chapter cụ thể
    public Chapter getChapterByStoryAndNumber(Long storyId, Integer chapterNumber) {
        return chapterRepository.findByStory_IdAndChapterNumber(storyId, chapterNumber)
                .orElse(null);
    }

    @Transactional
    public Chapter increaseChapterView(Long storyId, Integer chapterNumber) {

        Chapter chapter = chapterRepository
                .findByStory_IdAndChapterNumber(storyId, chapterNumber)
                .orElse(null);

        if (chapter == null)
            return null;

        // +1 view cho chapter
        chapter.setViews(chapter.getViews() + 1);

        // +1 view cho story
        if (chapter.getStory() != null) {
            chapter.getStory().setViews(
                    chapter.getStory().getViews() + 1);
        }

        // ❌ KHÔNG cần save() – Hibernate auto flush
        return chapter;
    }

    @Transactional
    public Chapter createChapter(Chapter chapter) {
        // 1. Lưu chapter mới
        Chapter savedChapter = chapterRepository.save(chapter);
        
        // 2. Cập nhật số lượng chương trong Story
        Story story = chapter.getStory();
        story.setChaptersCount(story.getChaptersCount() + 1);
        storyRepository.save(story);
        
        return savedChapter;
    }

}