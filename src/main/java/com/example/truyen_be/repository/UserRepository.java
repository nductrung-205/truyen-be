package com.example.truyen_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.truyen_be.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
