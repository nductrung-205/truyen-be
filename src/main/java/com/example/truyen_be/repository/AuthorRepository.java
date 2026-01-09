package com.example.truyen_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.truyen_be.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {}
