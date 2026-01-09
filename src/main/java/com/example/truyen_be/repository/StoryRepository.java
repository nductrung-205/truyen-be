package com.example.truyen_be.repository;
import com.example.truyen_be.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByOrderByViewsDesc(); // Lấy truyện xem nhiều nhất
    List<Story> findByCategories_Slug(String slug); // Lọc theo thể loại

    List<Story> findByTitleContainingIgnoreCase(String keyword);
}