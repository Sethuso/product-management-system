package com.example.productservice.controller;

import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductRequest productRequest;
    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategory(1L);

        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Success")
                .httpStatus(HttpStatus.OK.value())
                .build();
    }

    @Test
    void createProduct_Success() {
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(apiResponse);

        ApiResponse response = productController.createProduct(productRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void updateProduct_Success() {
        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(apiResponse);

        ApiResponse response = productController.updateProduct(1L, productRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequest.class));
    }

    @Test
    void getProductById_Success() {
        when(productService.getProductById(anyLong())).thenReturn(apiResponse);

        ApiResponse response = productController.getProductById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(productService, times(1)).getProductById(anyLong());
    }

    @Test
    void getAllProducts_Success() {
        when(productService.getAllProducts()).thenReturn(apiResponse);

        ApiResponse response = productController.getAllProducts();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void findAvailableProductsByCategory_Success() {
        when(productService.findAvailableProductsByCategory(anyString(), anyString())).thenReturn(apiResponse);

        ApiResponse response = productController.findAvailableProductsByCategory("Test Category", "name");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());
        verify(productService, times(1)).findAvailableProductsByCategory(anyString(), anyString());
    }
}