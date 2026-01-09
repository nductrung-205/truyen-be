package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapter_contents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChapterContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
}