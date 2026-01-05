package com.example.truyen_be.repository;

import com.example.truyen_be.models.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
}