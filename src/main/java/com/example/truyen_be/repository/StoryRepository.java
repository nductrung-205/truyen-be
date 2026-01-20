package com.example.truyen_be.repository;
import com.example.truyen_be.entity.Story;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long>, JpaSpecificationExecutor<Story> {
    List<Story> findByOrderByViewsDesc(); // Lấy truyện xem nhiều nhất
    List<Story> findByCategories_Slug(String slug); // Lọc theo thể loại

    List<Story> findByTitleContainingIgnoreCase(String keyword);

    Page<Story> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);
}