package com.example.truyen_be.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating; // 1-5
    private String content;
}