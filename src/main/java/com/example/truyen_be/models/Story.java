package com.example.truyen_be.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stories")
@Data 
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String thumbnail_url;
}