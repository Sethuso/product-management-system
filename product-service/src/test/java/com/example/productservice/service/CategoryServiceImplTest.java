package com.example.productservice.service;


import com.example.productservice.exception.ResourceNotFoundException;
import com.example.productservice.model.Category;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.serviceImpl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryRequest categoryRequest;
    private Category category;
    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Test Category");
        categoryRequest.setDescription("Test Description");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setCreatedAt(LocalDateTime.now());

        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Success")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Test
    void createCategory_Success() {
        when(modelMapper.map(any(CategoryRequest.class), eq(Category.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        ApiResponse response = categoryService.createCategory(categoryRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("Category created successfully", response.getMessage());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_Exception() {
        when(modelMapper.map(any(CategoryRequest.class), eq(Category.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequest);
        });

        assertEquals("Error creating category: Database error", exception.getMessage());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        ApiResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Category retrieved successfully", response.getMessage());
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(1L);
        });

        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllCategories_Success() {
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        ApiResponse response = categoryService.getAllCategories();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Categories retrieved successfully", response.getMessage());
        verify(categoryRepository, times(1)).findAll();
    }
}