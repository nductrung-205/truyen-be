package com.example.truyen_be.repository;

import com.example.truyen_be.entity.UnlockedChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnlockedChapterRepository extends JpaRepository<UnlockedChapter, Long> {
    
    // Phương thức kiểm tra xem một User đã mở khóa một Chapter cụ thể chưa
    boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}