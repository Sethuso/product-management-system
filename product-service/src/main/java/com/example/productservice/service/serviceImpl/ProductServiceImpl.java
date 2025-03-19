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
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PricingServiceFeignClient pricingServiceFeignClient;
    @Autowired
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    @Transactional
    public ApiResponse createProduct(ProductRequest productRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("Creating product: {} | TraceId: {}", productRequest.getName(), traceId);

            if (productRepository.findByName(productRequest.getName()).isPresent()) {
                return ApiResponse.failure("Product with the same name already exists", traceId, HttpStatus.CONFLICT);
            }

            Category category = categoryRepository.findById(productRequest.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));

            Product product = modelMapper.map(productRequest, Product.class);
            product.setCategory(category);
            product.setQuantity(0);
            product.setPrice(0.0);
            product.setCreatedAt(LocalDateTime.now());

            Product savedProduct = productRepository.save(product);
            ProductDto response = modelMapper.map(savedProduct, ProductDto.class);

            return ApiResponse.success(response, "Product created successfully", traceId, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Error creating product: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse updateProduct(Long productId, ProductRequest productRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (productRequest.getCategory() != null) {
                Category category = categoryRepository.findById(productRequest.getCategory())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));
                existingProduct.setCategory(category);
            }

            modelMapper.map(productRequest, existingProduct);
            existingProduct.setUpdatedAt(LocalDateTime.now());

            Product updatedProduct = productRepository.save(existingProduct);
            ProductDto response = modelMapper.map(updatedProduct, ProductDto.class);


            return ApiResponse.success(response, "Product updated successfully", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ApiResponse deleteProduct(Long productId) {
        String traceId = UUID.randomUUID().toString();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        productRepository.delete(product);
        return ApiResponse.success(null, "Product deleted successfully", traceId, HttpStatus.OK);
    }

    @Override
    public ApiResponse getAllProducts() {
        String traceId = UUID.randomUUID().toString();
        List<ProductDto> products = productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        return ApiResponse.success(products, "Products retrieved successfully", traceId, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse getProductById(Long productId) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Fetching product details for Product ID: {}", traceId, productId);

            // Fetch product from the database
            Optional<Product> productOpt = productRepository.findById(productId);

            if (productOpt.isEmpty()) {
                log.error("[{}] Product not found for Product ID: {}", traceId, productId);
                return ApiResponse.failure("Product not found", traceId, HttpStatus.NOT_FOUND);
            }

            Product product = productOpt.get();

            // Fetch price from Pricing Service
            fetchPriceFromPricingService(product, productId, traceId);

            // Fetch inventory from Inventory Service
            fetchInventoryFromInventoryService(product, productId, traceId);

            // Save the updated product entity to the database
            productRepository.save(product);

            log.info("[{}] Product details retrieved successfully for Product ID: {}", traceId, productId);
            return ApiResponse.success(product, "Product retrieved successfully with price and inventory", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve product details", ex);
        }
    }


    // Helper method to fetch price from Pricing Service
    private void fetchPriceFromPricingService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
            ApiResponse priceResponse = pricingServiceFeignClient.getPriceByProductId(productId);

            if (priceResponse != null && priceResponse.getData() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                PriceResponse price = objectMapper.convertValue(priceResponse.getData(), PriceResponse.class);

                if (price != null) {
                    product.setPrice(price.getPrice());
//                    productRepository.save(product);
                    log.info("[{}] Price successfully retrieved and attached to product: {}", traceId, price.getPrice());
                }
            } else {
                log.warn("[{}] Price not found for Product ID: {}. Proceeding without price.", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage());
        }
    }

    // Helper method to fetch inventory from Inventory Service
    private void fetchInventoryFromInventoryService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
            ApiResponse inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId);

            if (inventoryResponse != null && inventoryResponse.getData() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                InventoryResponse inventory = objectMapper.convertValue(inventoryResponse.getData(), InventoryResponse.class);

                if (inventory != null) {
                    product.setQuantity(inventory.getQuantity());
//                    productRepository.save(product);
                    log.info("[{}] Inventory successfully retrieved and attached to product: {}", traceId, inventory.getQuantity());
                }
            } else {
                log.warn("[{}] Inventory not found for Product ID: {}. Proceeding without inventory.", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching inventory for Product ID: {}. Error: {}", traceId, productId, e.getMessage());
        }
    }

    @Override
    public ApiResponse findAvailableProductsByCategory(String categoryName, String sortBy) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Fetching available products for category: '{}'", traceId, categoryName);

            // Trim and validate the category name
            if (categoryName == null || categoryName.trim().isEmpty()) {
                log.error("[{}] Category name is null or empty.", traceId);
                return ApiResponse.failure("Category name must not be null or empty.", traceId, HttpStatus.BAD_REQUEST);
            }
            categoryName = categoryName.trim();

            // Check if the category exists
            Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
            if (categoryOpt.isEmpty()) {
                log.error("[{}] Category '{}' not found.", traceId, categoryName);
                return ApiResponse.failure("Category '" + categoryName + "' not found.", traceId, HttpStatus.NOT_FOUND);
            }

            // Fetch products based on category
            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName, sortBy);

            // Update each product with price and inventory details
            for (Product product : products) {
                fetchPriceFromPricingService(product, product.getId(), traceId);
                fetchInventoryFromInventoryService(product, product.getId(), traceId);
            }

            // Filter out products with unavailable or limited inventory
            List<ProductDto> productResponses = products.stream()
                    .filter(product -> product.getQuantity() > 0) // Assuming '0' indicates unavailable inventory
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .collect(Collectors.toList());

            // Handle case where no products are available after filtering
            if (productResponses.isEmpty()) {
                log.warn("[{}] No available products found for category: '{}'", traceId, categoryName);
                return ApiResponse.failure("No available products found for category '" + categoryName + "'.", traceId, HttpStatus.OK);
            }

            log.info("[{}] Products retrieved successfully for category: '{}'", traceId, categoryName);
            return ApiResponse.success(productResponses, "Products retrieved successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage());
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
