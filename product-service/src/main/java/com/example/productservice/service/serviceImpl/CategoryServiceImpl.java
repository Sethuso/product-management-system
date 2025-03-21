package com.example.productservice.service.serviceImpl;

import com.example.productservice.dto.CategoryDto;
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

            // Save category to the database
            Category savedCategory = categoryRepository.save(category);

            // Convert saved Category to CategoryDto
            CategoryDto categoryDto = modelMapper.map(savedCategory, CategoryDto.class);

            logger.info("Category created successfully with ID: {} | TraceId: {}", savedCategory.getId(), traceId);

            return ApiResponse.success(categoryDto, "Category created successfully", traceId, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error creating category: {} | TraceId: {}", e.getMessage(), traceId);
            throw new RuntimeException("Error creating category: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse getCategoryById(Long categoryId) {
        String traceId = UUID.randomUUID().toString();
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

            logger.info("Category retrieved successfully with ID: {} | TraceId: {}", categoryId, traceId);

            return ApiResponse.success(category, "Category retrieved successfully", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            logger.error("Category not found with ID: {} | TraceId: {}", categoryId, traceId);
            throw e;
        }
    }

    @Override
    public ApiResponse getAllCategories() {
        String traceId = UUID.randomUUID().toString();
        try {
            var categories = categoryRepository.findAll();
            logger.info("All categories retrieved successfully | TraceId: {}", traceId);

            return ApiResponse.success(categories, "Categories retrieved successfully", traceId, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error retrieving categories: {} | TraceId: {}", e.getMessage(), traceId);
            throw new RuntimeException("Error retrieving categories: " + e.getMessage());
        }
    }
}
