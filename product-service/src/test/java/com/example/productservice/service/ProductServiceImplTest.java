package com.example.productservice.service;


import com.example.productservice.dto.ProductDto;
import com.example.productservice.feignclient.InventoryServiceFeignClient;
import com.example.productservice.feignclient.PricingServiceFeignClient;
import com.example.productservice.model.Category;
import com.example.productservice.model.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.InventoryResponse;
import com.example.productservice.response.PriceResponse;
import com.example.productservice.service.serviceImpl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PricingServiceFeignClient pricingServiceFeignClient;

    @Mock
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private Category category;
    private ProductDto productDto;
    private PriceResponse priceResponse;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategory(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());

        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setCategory(category.getId());

        priceResponse = new PriceResponse();
        priceResponse.setPrice(100.0);

        inventoryResponse = new InventoryResponse();
        inventoryResponse.setQuantity(10);
    }

    @Test
    void createProduct_Success() {
        when(productRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(modelMapper.map(any(ProductRequest.class), eq(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(productDto);

        ApiResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getHttpStatus());
        assertEquals("Product created successfully", response.getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ProductAlreadyExists() {
        when(productRepository.findByName(anyString())).thenReturn(Optional.of(product));

        ApiResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getHttpStatus());
        assertEquals("Product with the same name already exists", response.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_CategoryNotFound() {
        when(productRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Category not found with ID: 1", response.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(productDto);

        ApiResponse response = productService.updateProduct(1L, productRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Product updated successfully", response.getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = productService.updateProduct(1L, productRequest);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found with ID: 1", response.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ApiResponse response = productService.deleteProduct(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Product deleted successfully", response.getMessage());
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = productService.deleteProduct(1L);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found with ID: 1", response.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(productDto);

        ApiResponse response = productService.getAllProducts();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertEquals(1, ((List<?>) response.getData()).size());
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(pricingServiceFeignClient.getPriceByProductId(anyLong())).thenReturn(ApiResponse.success(priceResponse, "Price retrieved", UUID.randomUUID().toString(), HttpStatus.OK));
        when(inventoryServiceFeignClient.getInventoryByProductId(anyLong())).thenReturn(ApiResponse.success(inventoryResponse, "Inventory retrieved", UUID.randomUUID().toString(), HttpStatus.OK));

        ApiResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Product retrieved successfully with price and inventory", response.getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void findAvailableProductsByCategory_Success() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
        when(productRepository.findAvailableProductsByCategoryName(anyString(), anyString())).thenReturn(Collections.singletonList(product));
        when(pricingServiceFeignClient.getPriceByProductId(anyLong())).thenReturn(ApiResponse.success(priceResponse, "Price retrieved", UUID.randomUUID().toString(), HttpStatus.OK));
        when(inventoryServiceFeignClient.getInventoryByProductId(anyLong())).thenReturn(ApiResponse.success(inventoryResponse, "Inventory retrieved", UUID.randomUUID().toString(), HttpStatus.OK));
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(productDto);

        ApiResponse response = productService.findAvailableProductsByCategory("Test Category", "name");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertEquals(1, ((List<?>) response.getData()).size());
    }

    @Test
    void findAvailableProductsByCategory_CategoryNotFound() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        ApiResponse response = productService.findAvailableProductsByCategory("Test Category", "name");

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Category 'Test Category' not found.", response.getMessage());
    }

    @Test
    void findAvailableProductsByCategory_NoProductsAvailable() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
        when(productRepository.findAvailableProductsByCategoryName(anyString(), anyString())).thenReturn(Collections.emptyList());

        ApiResponse response = productService.findAvailableProductsByCategory("Test Category", "name");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("No available products found for category 'Test Category'.", response.getMessage());
    }
}