package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String description;
    private String icon;

    @ManyToMany(mappedBy = "categories")
    private Set<Story> stories;
}