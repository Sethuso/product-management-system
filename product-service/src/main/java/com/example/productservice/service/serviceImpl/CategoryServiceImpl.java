package com.example.productservice.service.serviceImpl;

import com.example.productservice.exception.ResourceNotFoundException;
import com.example.productservice.model.Category;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    @Transactional
    public ApiResponse createCategory(CategoryRequest categoryRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            logger.info("Creating category: {} | TraceId: {}", categoryRequest.getName(), traceId);

            // Map CategoryRequest to Category entity using ModelMapper
            Category category = modelMapper.map(categoryRequest, Category.class);

            // Set additional fields
            category.setCreatedAt(LocalDateTime.now());

            // Save category to the database
            Category savedCategory = categoryRepository.save(category);

            logger.info("Category created successfully with ID: {} | TraceId: {}", savedCategory.getId(), traceId);

            return ApiResponse.success(savedCategory, "Category created successfully", traceId, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error creating category: {} | TraceId: {}", e.getMessage(), traceId);
            throw new RuntimeException("Error creating category: " + e.getMessage());
        }
    }


    @Override
    public ApiResponse getCategoryById(Long categoryId) {
        String traceId = UUID.randomUUID().toString();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        return ApiResponse.success(category, "Category retrieved successfully", traceId, HttpStatus.OK);
    }

    @Override
    public ApiResponse getAllCategories() {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.success(categoryRepository.findAll(), "Categories retrieved successfully", traceId, HttpStatus.OK);
    }
}
