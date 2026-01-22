package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "unlocked_chapters")
@Getter @Setter @NoArgsConstructor
public class UnlockedChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private LocalDateTime unlockedAt = LocalDateTime.now();
}
