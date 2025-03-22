package com.example.productservice.service;

import com.example.productservice.model.Category;
import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import jakarta.validation.constraints.NotNull;


public interface CategoryService {

    ApiResponse createCategory(CategoryRequest category);

    ApiResponse getCategoryById(Long categoryId);

    ApiResponse getAllCategories();

    Category getCategoryByName(String categoryName);

    Category findById(Long category);
}
