package com.example.productservice.service.serviceImpl;

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
import com.example.productservice.dto.ProductDto;
import com.example.productservice.service.CategoryService;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PricingServiceFeignClient pricingServiceFeignClient;

    @Autowired
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Autowired
    private ObjectMapper objectMapper;



    @Override
    @Transactional
    public ApiResponse createProduct(ProductRequest productRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Creating product: {}", traceId, productRequest.getName());

            // Check if product with the same name already exists
            if (productRepository.findByName(productRequest.getName()).isPresent()) {
                return ApiResponse.failure("Product with the same name already exists", traceId, HttpStatus.CONFLICT);
            }
// Fetch category
            Category category = categoryService.findById(productRequest.getCategory());

            // Map request to product entity
            Product product = modelMapper.map(productRequest, Product.class);
            product.setCategory(category);
            // Save product
            Product savedProduct = productRepository.save(product);
            ProductDto response = modelMapper.map(savedProduct, ProductDto.class);

            return ApiResponse.success(response, "Product created successfully", traceId, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error creating product: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error creating product: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to create product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse updateProduct(Long productId, ProductRequest productRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            // Fetch existing product
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            // Update category if provided
            if (productRequest.getCategory() != null) {

                Category category = categoryService.findById(productRequest.getCategory());
                existingProduct.setCategory(category);
            }

            // Map request to existing product
            modelMapper.map(productRequest, existingProduct);
            existingProduct.setUpdatedAt(LocalDateTime.now());

            // Save updated product
            Product updatedProduct = productRepository.save(existingProduct);
            ProductDto response = modelMapper.map(updatedProduct, ProductDto.class);

            return ApiResponse.success(response, "Product updated successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error updating product: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error updating product: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to update product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse deleteProduct(Long productId) {
        String traceId = UUID.randomUUID().toString();
        try {
            // Fetch product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            // Delete product
            productRepository.delete(product);

            return ApiResponse.success(null, "Product deleted successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error deleting product: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error deleting product: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to delete product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse getAllProducts() {
        String traceId = UUID.randomUUID().toString();
        try {
            // Fetch all products
            List<ProductDto> products = productRepository.findAll().stream()
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .collect(Collectors.toList());

            return ApiResponse.success(products, "Products retrieved successfully", traceId, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("[{}] Error fetching all products: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<List<ProductDto>> getProductsByCategory(String categoryName) {
        String traceId = UUID.randomUUID().toString(); // Generate a trace ID for tracking requests

        try {
            // Fetch category by name
            Category category = categoryService.getCategoryByName(categoryName);

            if (category == null) {
                log.warn("Category not found with name: {}", categoryName);
                return ApiResponse.failure("Category not found with name: " + categoryName, traceId, HttpStatus.NOT_FOUND);
            }

            // Fetch available products by category name
            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName);

            if (products.isEmpty()) {
                log.warn("No products available with sufficient inventory for category: {}", categoryName);
                return ApiResponse.failure("No products available with sufficient inventory in the requested category.", traceId, HttpStatus.NOT_FOUND);
            }

            // Map Product entities to ProductDto
            List<ProductDto> productDtos = products.stream()
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .toList();

            log.info("Products fetched successfully for category: {}", categoryName);
            return ApiResponse.success(productDtos, "Products fetched successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error while fetching products for category: {}", categoryName, ex);
            return ApiResponse.failure("An error occurred while fetching products. Please try again later.", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<ProductDto> getProductById(Long productId) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Fetching product details for Product ID: {}", traceId, productId);

            // Fetch product from the database
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("[{}] Product not found for Product ID: {}", traceId, productId);
                        return new ResourceNotFoundException("Product not found with ID: " + productId);
                    });

            log.info("[{}] Product details retrieved successfully for Product ID: {}", traceId, productId);

            // Create an empty ProductDto object to populate later
            ProductDto productDto = modelMapper.map(product, ProductDto.class);

            // Fetch price and inventory in parallel
            CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> fetchPriceFromPricingService(productDto, productId, traceId));
            CompletableFuture<Void> inventoryFuture = CompletableFuture.runAsync(() -> fetchInventoryFromInventoryService(productDto, productId, traceId));

            // Wait for both futures to complete
            CompletableFuture.allOf(priceFuture, inventoryFuture).join();

            return ApiResponse.success(productDto, "Product retrieved successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Product not found: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to retrieve product details", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse findAvailableProductsByCategory(String categoryName, String sortBy) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Fetching available products for category: '{}'", traceId, categoryName);

            // Validate category name
            if (categoryName == null || categoryName.trim().isEmpty()) {
                log.error("[{}] Category name is null or empty.", traceId);
                return ApiResponse.failure("Category name must not be null or empty.", traceId, HttpStatus.BAD_REQUEST);
            }

            categoryName = categoryName.trim();
            String finalCategoryName = categoryName;
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new ResourceNotFoundException("Category '" + finalCategoryName + "' does not exist"));


            // Handle sortBy parameter (defaulting to 'low' if invalid)
            if (!"low".equalsIgnoreCase(sortBy) && !"high".equalsIgnoreCase(sortBy)) {
                log.warn("[{}] Invalid sortBy parameter '{}'. Defaulting to 'low'.", traceId, sortBy);
                sortBy = "low";
            }

            // Fetch products by category
            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName);
            if (products.isEmpty()) {
                log.warn("[{}] No products found for category: '{}'.", traceId, categoryName);
                throw new ResourceNotFoundException("No products found for category: " + categoryName);
            }

            List<ProductDto> availableProducts = new ArrayList<>();

            for (Product product : products) {
                ProductDto productDto = modelMapper.map(product, ProductDto.class);
                productDto.setCategory(categoryName); // Set category name in ProductDto

                CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() ->
                        fetchPriceFromPricingService(productDto, product.getId(), traceId));

                CompletableFuture<Void> inventoryFuture = CompletableFuture.runAsync(() ->
                        fetchInventoryFromInventoryService(productDto, product.getId(), traceId));

                CompletableFuture.allOf(priceFuture, inventoryFuture).join();

                if ("In Stock".equalsIgnoreCase(productDto.getQuantityStatus())) { // Safe comparison
                    availableProducts.add(productDto);
                }
            }

            if (availableProducts.isEmpty()) {
                log.warn("[{}] All products in category '{}' are out of stock.", traceId, categoryName);
                throw new ResourceNotFoundException("All products in category '" + categoryName + "' are out of stock.");
            }

            if ("low".equalsIgnoreCase(sortBy)) {
                availableProducts.sort(Comparator.comparingDouble(ProductDto::getPrice));
            } else {
                availableProducts.sort(Comparator.comparingDouble(ProductDto::getPrice).reversed());
            }

            log.info("[{}] Products retrieved successfully for category: '{}'.", traceId, categoryName);
            return ApiResponse.success(availableProducts, "Products retrieved successfully", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void fetchPriceFromPricingService(ProductDto productDto, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
            ApiResponse<PriceResponse> priceResponse = pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE");

            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
                PriceResponse price = priceResponse.getData();
                productDto.setPrice(price.getPrice());
                log.info("[{}] Price successfully retrieved for Product ID: {}", traceId, productId);
            } else {
                log.warn("[{}] Price not found for Product ID: {}", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
        }
    }

    private void fetchInventoryFromInventoryService(ProductDto productDto, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
            ApiResponse<InventoryResponse> inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE");

            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
                InventoryResponse inventory = inventoryResponse.getData();
                if (inventory.getQuantity() > 0) {
                    productDto.setQuantityStatus("In Stock");
                } else {
                    productDto.setQuantityStatus("Out of Stock");
                }
                log.info("[{}] Inventory successfully retrieved for Product ID: {}", traceId, productId);
            } else {
                log.warn("[{}] Inventory not found for Product ID: {}", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching inventory for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
        }
    }

}