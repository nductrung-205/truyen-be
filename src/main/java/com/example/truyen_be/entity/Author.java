package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "author")
    private List<Story> stories;
}