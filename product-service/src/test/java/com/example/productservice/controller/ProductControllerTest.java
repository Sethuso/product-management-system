package com.example.productservice.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createProduct endpoint

    @Test
    public void testCreateProduct_Success() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");

        ApiResponse expectedResponse = ApiResponse.success(new ProductDto(), "Product created successfully", UUID.randomUUID().toString(), HttpStatus.CREATED);
        when(productService.createProduct(productRequest)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals("Product created successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateProduct_Exception() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");

        when(productService.createProduct(productRequest)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to create product"));
    }

    // Test cases for updateProduct endpoint

    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product");

        ApiResponse expectedResponse = ApiResponse.success(new ProductDto(), "Product updated successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(productService.updateProduct(productId, productRequest)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.updateProduct(productId, productRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Product updated successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateProduct_Exception() {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product");

        when(productService.updateProduct(productId, productRequest)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.updateProduct(productId, productRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to update product"));
    }

    // Test cases for getProductById endpoint

    @Test
    public void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        String serviceName = "PRODUCT-SERVICE";

        ApiResponse expectedResponse = ApiResponse.success(new ProductDto(), "Product retrieved successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(productService.getProductById(productId)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.getProductById(productId, serviceName);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Product retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetProductById_Exception() {
        // Arrange
        Long productId = 1L;
        String serviceName = "PRODUCT-SERVICE";

        when(productService.getProductById(productId)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.getProductById(productId, serviceName);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to retrieve product details"));
    }

    // Test cases for getAllProducts endpoint

    @Test
    public void testGetAllProducts_Success() {
        // Arrange
        ApiResponse expectedResponse = ApiResponse.success(List.of(new ProductDto()), "Products retrieved successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(productService.getAllProducts()).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetAllProducts_Exception() {
        // Arrange
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to retrieve products"));
    }

    // Test cases for findAvailableProductsByCategory endpoint

    @Test
    public void testFindAvailableProductsByCategory_Success() {
        // Arrange
        String categoryName = "Electronics";
        String priceRange = "low";

        ApiResponse expectedResponse = ApiResponse.success(List.of(new ProductDto()), "Products retrieved successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(productService.findAvailableProductsByCategory(categoryName, priceRange)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.findAvailableProductsByCategory(categoryName, priceRange);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testFindAvailableProductsByCategory_Exception() {
        // Arrange
        String categoryName = "Electronics";
        String priceRange = "low";

        when(productService.findAvailableProductsByCategory(categoryName, priceRange)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.findAvailableProductsByCategory(categoryName, priceRange);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to retrieve products"));
    }

    // Test cases for getProductsByCategory endpoint

    @Test
    public void testGetProductsByCategory_Success() {
        // Arrange
        String categoryName = "Electronics";

        ApiResponse expectedResponse = ApiResponse.success(List.of(new ProductDto()), "Products fetched successfully", UUID.randomUUID().toString(), HttpStatus.OK);
        when(productService.getProductsByCategory(categoryName)).thenReturn(expectedResponse);

        // Act
        ApiResponse response = productController.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products fetched successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetProductsByCategory_Exception() {
        // Arrange
        String categoryName = "Electronics";

        when(productService.getProductsByCategory(categoryName)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = productController.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to fetch products"));
    }
}