package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true)
    private String slug;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status = "Đang ra";
    private Long views = 0L;
    private Double rating = 0.0;

    @Column(name = "chapters_count")
    private Integer chaptersCount = 0;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(
        name = "story_categories",
        joinColumns = @JoinColumn(name = "story_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<Chapter> chapters;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
