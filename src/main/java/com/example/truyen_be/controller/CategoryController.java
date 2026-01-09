package com.example.truyen_be.controller;

import com.example.truyen_be.dto.CategoryDTO;
import com.example.truyen_be.entity.Category;
import com.example.truyen_be.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả thể loại
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> dtos = categoryService.getAllCategories()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Lấy chi tiết thể loại
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return category != null
                ? ResponseEntity.ok(convertToDTO(category))
                : ResponseEntity.notFound().build();
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setStoryCount(category.getStories() != null ? category.getStories().size() : 0);
        return dto;
    }
}