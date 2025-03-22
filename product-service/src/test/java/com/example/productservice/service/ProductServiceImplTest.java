package com.example.productservice.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.exception.ResourceNotFoundException;
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
import com.example.productservice.service.CategoryService;
import com.example.productservice.service.serviceImpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryService categoryService;

    @Mock
    private PricingServiceFeignClient pricingServiceFeignClient;

    @Mock
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createProduct method

    @Test
    public void testCreateProduct_Success() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategory(1L);

        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setCategory(category);

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");

        when(productRepository.findByName("Test Product")).thenReturn(Optional.empty());
        when(categoryService.findById(1L)).thenReturn(category);
        when(modelMapper.map(productRequest, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        // Act
        ApiResponse response = productService.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.resolve(201), response.getHttpStatus());
        assertEquals("Product created successfully", response.getMessage());
        assertNotNull(response.getData());

    }

    @Test
    public void testCreateProduct_ProductAlreadyExists() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");

        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(new Product()));

        // Act
        ApiResponse response = productService.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getHttpStatus());
        assertEquals("Product with the same name already exists", response.getMessage());
    }

    @Test
    public void testCreateProduct_CategoryNotFound() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategory(1L);

        when(productRepository.findByName("Test Product")).thenReturn(Optional.empty());
        when(categoryService.findById(1L)).thenThrow(new ResourceNotFoundException("Category not found"));

        // Act
        ApiResponse response = productService.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Category not found", response.getMessage());
    }

    @Test
    public void testCreateProduct_Exception() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");

        when(productRepository.findByName("Test Product")).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.createProduct(productRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to create product", response.getMessage());
    }

    // Test cases for updateProduct method

    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product");
        productRequest.setCategory(2L);

        Category category = new Category();
        category.setId(2L);

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Test Product");

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setCategory(category);

        ProductDto productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryService.findById(2L)).thenReturn(category);
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(modelMapper.map(updatedProduct, ProductDto.class)).thenReturn(productDto);

        // Act
        ApiResponse response = productService.updateProduct(productId, productRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Product updated successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = productService.updateProduct(productId, productRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Product not found with ID: " + productId, response.getMessage());
    }

    @Test
    public void testUpdateProduct_Exception() {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();

        when(productRepository.findById(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.updateProduct(productId, productRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to update product", response.getMessage());
    }

    // Test cases for deleteProduct method

    @Test
    public void testDeleteProduct_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ApiResponse response = productService.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Product deleted successfully", response.getMessage());
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = productService.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Product not found with ID: " + productId, response.getMessage());
    }

    @Test
    public void testDeleteProduct_Exception() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to delete product", response.getMessage());
    }

    // Test cases for getAllProducts method

    @Test
    public void testGetAllProducts_Success() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        List<Product> products = List.of(product1, product2);

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(product1, ProductDto.class)).thenReturn(new ProductDto());
        when(modelMapper.map(product2, ProductDto.class)).thenReturn(new ProductDto());

        // Act
        ApiResponse response = productService.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetAllProducts_Exception() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.getAllProducts();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to retrieve products", response.getMessage());
    }

    // Test cases for getProductsByCategory method

    @Test
    public void testGetProductsByCategory_Success() {
        // Arrange
        String categoryName = "Electronics";
        Category category = new Category();
        category.setName(categoryName);

        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        List<Product> products = List.of(product1, product2);

        when(categoryService.getCategoryByName(categoryName)).thenReturn(category);
        when(productRepository.findAvailableProductsByCategoryName(categoryName)).thenReturn(products);
        when(modelMapper.map(product1, ProductDto.class)).thenReturn(new ProductDto());
        when(modelMapper.map(product2, ProductDto.class)).thenReturn(new ProductDto());

        // Act
        ApiResponse response = productService.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products fetched successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetProductsByCategory_CategoryNotFound() {
        // Arrange
        String categoryName = "Electronics";

        when(categoryService.getCategoryByName(categoryName)).thenReturn(null);

        // Act
        ApiResponse response = productService.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Category not found with name: " + categoryName, response.getMessage());
    }

    @Test
    public void testGetProductsByCategory_NoProductsFound() {
        // Arrange
        String categoryName = "Electronics";
        Category category = new Category();
        category.setName(categoryName);

        when(categoryService.getCategoryByName(categoryName)).thenReturn(category);
        when(productRepository.findAvailableProductsByCategoryName(categoryName)).thenReturn(List.of());

        // Act
        ApiResponse response = productService.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("No products available with sufficient inventory in the requested category.", response.getMessage());
    }

    @Test
    public void testGetProductsByCategory_Exception() {
        // Arrange
        String categoryName = "Electronics";

        when(categoryService.getCategoryByName(categoryName)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.getProductsByCategory(categoryName);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("An error occurred while fetching products. Please try again later.", response.getMessage());
    }

    // Test cases for getProductById method

    @Test
    public void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        ProductDto productDto = new ProductDto();
        productDto.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        when(pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE")).thenReturn(ApiResponse.success(new PriceResponse(), "Success", UUID.randomUUID().toString(), HttpStatus.OK));
        when(inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE")).thenReturn(ApiResponse.success(new InventoryResponse(), "Success", UUID.randomUUID().toString(), HttpStatus.OK));

        // Act
        ApiResponse response = productService.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Product retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = productService.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Product not found with ID: " + productId, response.getMessage());
    }

    @Test
    public void testGetProductById_Exception() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to retrieve product details", response.getMessage());
    }

    // Test cases for findAvailableProductsByCategory method

    @Test
    public void testFindAvailableProductsByCategory_Success() {
        // Arrange
        String categoryName = "Electronics";
        String sortBy = "low";

        Category category = new Category();
        category.setName(categoryName);

        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        List<Product> products = List.of(product1, product2);

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(productRepository.findAvailableProductsByCategoryName(categoryName)).thenReturn(products);
        when(modelMapper.map(product1, ProductDto.class)).thenReturn(new ProductDto());
        when(modelMapper.map(product2, ProductDto.class)).thenReturn(new ProductDto());

        // Act
        ApiResponse response = productService.findAvailableProductsByCategory(categoryName, sortBy);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Products retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testFindAvailableProductsByCategory_CategoryNotFound() {
        // Arrange
        String categoryName = "Electronics";
        String sortBy = "low";

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = productService.findAvailableProductsByCategory(categoryName, sortBy);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Category '" + categoryName + "' does not exist", response.getMessage());
    }

    @Test
    public void testFindAvailableProductsByCategory_NoProductsFound() {
        // Arrange
        String categoryName = "Electronics";
        String sortBy = "low";

        Category category = new Category();
        category.setName(categoryName);

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(productRepository.findAvailableProductsByCategoryName(categoryName)).thenReturn(List.of());

        // Act
        ApiResponse response = productService.findAvailableProductsByCategory(categoryName, sortBy);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("No products found for category: " + categoryName, response.getMessage());
    }

    @Test
    public void testFindAvailableProductsByCategory_Exception() {
        // Arrange
        String categoryName = "Electronics";
        String sortBy = "low";

        when(categoryRepository.findByName(categoryName)).thenThrow(new RuntimeException("Database Error"));

        // Act
        ApiResponse response = productService.findAvailableProductsByCategory(categoryName, sortBy);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to retrieve products", response.getMessage());
    }
}