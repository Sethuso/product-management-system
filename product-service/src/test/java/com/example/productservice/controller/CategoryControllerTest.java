package com.example.productservice.controller;

import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryRequest categoryRequest;
    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Test Category");
        categoryRequest.setDescription("Test Description");

        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Success")
                .httpStatus(HttpStatus.OK.value())
                .build();
    }

    @Test
    void createCategory_Success() {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(apiResponse);

        ApiResponse response = categoryController.createCategory(categoryRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryService.getCategoryById(anyLong())).thenReturn(apiResponse);

        ApiResponse response = categoryController.getCategoryById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(categoryService, times(1)).getCategoryById(anyLong());
    }

    @Test
    void getAllCategories_Success() {
        when(categoryService.getAllCategories()).thenReturn(apiResponse);

        ApiResponse response = categoryController.getAllCategories();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(categoryService, times(1)).getAllCategories();
    }
}