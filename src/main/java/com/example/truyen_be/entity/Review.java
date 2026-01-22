package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người đánh giá

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story; // Truyện được đánh giá

    @Column(nullable = false)
    private Integer rating; // Số sao (1-5)

    @Column(columnDefinition = "TEXT")
    private String content; // Nội dung bình luận

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
