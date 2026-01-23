package com.example.truyen_be.service;

import com.example.truyen_be.entity.Story;
import com.example.truyen_be.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoryService {
    @Autowired
    private StoryRepository storyRepository;

    // Lấy tất cả truyện (không phân trang)
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    // Lấy tất cả truyện có phân trang
    public Page<Story> getAllStories(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return storyRepository.findAll(pageable);
    }

    // Lấy truyện hot (xem nhiều nhất)
    public List<Story> getHotStories() {
        return storyRepository.findByOrderByViewsDesc();
    }

    // Lấy chi tiết truyện
    public Story getStoryById(Long id) {
        return storyRepository.findById(id).orElse(null);
    }

    // Tìm kiếm theo title
    public List<Story> searchStoriesByTitle(String keyword) {
        return storyRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Lọc theo thể loại
    public List<Story> getStoriesByCategory(String categorySlug) {
        return storyRepository.findByCategories_Slug(categorySlug);
    }

    public Page<Story> getTopStoriesByPeriod(LocalDateTime fromDate, int page, int size) {
        // Thay vì dùng findByCreatedAtAfter, hãy dùng findAll và sắp xếp theo Views
        // Nếu bạn muốn lấy top chung.
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views"));
        return storyRepository.findAll(pageable);
    }

}