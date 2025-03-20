package com.example.productservice.controller;

import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/com/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ApiResponse createCategory(@Valid @RequestBody CategoryRequest category) {
        logger.info("Request to create category: {}", category.getName());
        return categoryService.createCategory(category);
    }

    @GetMapping
    public ApiResponse getCategoryById(@RequestParam Long id) {
        logger.info("Request to retrieve category with ID: {}", id);
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/all")
    public ApiResponse getAllCategories() {
        logger.info("Request to retrieve all categories");
        return categoryService.getAllCategories();
    }
}
