package com.example.truyen_be.service;

import com.example.truyen_be.entity.Chapter;
import com.example.truyen_be.entity.Story;
import com.example.truyen_be.entity.UnlockedChapter;
import com.example.truyen_be.entity.User;
import com.example.truyen_be.repository.ChapterRepository;
import com.example.truyen_be.repository.StoryRepository;
import com.example.truyen_be.repository.UnlockedChapterRepository;
import com.example.truyen_be.repository.UserRepository;

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
        // Tự động tính toán VIP: 2 chương cuối của mỗi block 10 chương là VIP
        // Ví dụ: Chương 9, 10, 19, 20, 29, 30...
        int num = chapter.getChapterNumber();
        if (num % 10 == 9 || num % 10 == 0) {
            chapter.setVip(true);
            chapter.setPrice(10); // Giá mặc định 10 xu
        } else {
            chapter.setVip(false);
            chapter.setPrice(0);
        }

        Chapter savedChapter = chapterRepository.save(chapter);

        Story story = chapter.getStory();
        story.setChaptersCount(story.getChaptersCount() + 1);
        storyRepository.save(story);
        return savedChapter;
    }

    @Autowired
    private UnlockedChapterRepository unlockedChapterRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isChapterUnlocked(Long userId, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        if (!chapter.isVip())
            return true; // Chương miễn phí
        if (userId == null)
            return false;

        // Kiểm tra xem đã mua chưa
        return unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapterId);
    }

    @Transactional
    public void unlockChapter(Long userId, Long chapterId) {
        User user = userRepository.findById(userId).orElseThrow();
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        if (isChapterUnlocked(userId, chapterId))
            return;

        if (user.getCoins() < chapter.getPrice()) {
            throw new RuntimeException("Bạn không đủ xu để mở khóa chương này!");
        }

        // Trừ xu
        user.setCoins(user.getCoins() - chapter.getPrice());
        userRepository.save(user);

        // Lưu vào bảng đã mở khóa
        UnlockedChapter unlock = new UnlockedChapter();
        unlock.setUser(user);
        unlock.setChapter(chapter);
        unlockedChapterRepository.save(unlock);
    }

}