//package com.example.productservice.service.serviceImpl;
//
//import com.example.productservice.exception.ResourceNotFoundException;
//import com.example.productservice.feignclient.InventoryServiceFeignClient;
//import com.example.productservice.feignclient.PricingServiceFeignClient;
//import com.example.productservice.model.Category;
//import com.example.productservice.model.Product;
//import com.example.productservice.repository.CategoryRepository;
//import com.example.productservice.repository.ProductRepository;
//import com.example.productservice.request.ProductRequest;
//import com.example.productservice.response.ApiResponse;
//import com.example.productservice.response.InventoryResponse;
//import com.example.productservice.response.PriceResponse;
//import com.example.productservice.dto.ProductDto;
//import com.example.productservice.service.ProductService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
//@Service
//public class ProductServiceImpl implements ProductService {
//
//    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private PricingServiceFeignClient pricingServiceFeignClient;
//
//    @Autowired
//    private InventoryServiceFeignClient inventoryServiceFeignClient;
//
//    @Autowired
//    private ObjectMapper objectMapper; // Autowired ObjectMapper bean
//
//    @Override
//    @Transactional
//    public ApiResponse createProduct(ProductRequest productRequest) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            log.info("Creating product: {} | TraceId: {}", productRequest.getName(), traceId);
//
//            // Check if product with the same name already exists
//            if (productRepository.findByName(productRequest.getName()).isPresent()) {
//                return ApiResponse.failure("Product with the same name already exists", traceId, HttpStatus.CONFLICT);
//            }
//
//            // Fetch category
//            Category category = categoryRepository.findById(productRequest.getCategory())
//                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));
//
//            // Map request to product entity
//            Product product = modelMapper.map(productRequest, Product.class);
//            product.setCategory(category);
//            product.setQuantity(0); // Default quantity
//            product.setPrice(0.0); // Default price
//
//            // Save product
//            Product savedProduct = productRepository.save(product);
//            ProductDto response = modelMapper.map(savedProduct, ProductDto.class);
//
//            return ApiResponse.success(response, "Product created successfully", traceId, HttpStatus.CREATED);
//        } catch (ResourceNotFoundException ex) {
//            log.error("Error creating product: {}", ex.getMessage());
//            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
//        } catch (Exception ex) {
//            log.error("Error creating product: {}", ex.getMessage());
//            return ApiResponse.failure("Failed to create product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Override
//    @Transactional
//    public ApiResponse updateProduct(Long productId, ProductRequest productRequest) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            // Fetch existing product
//            Product existingProduct = productRepository.findById(productId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
//
//            // Update category if provided
//            if (productRequest.getCategory() != null) {
//                Category category = categoryRepository.findById(productRequest.getCategory())
//                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));
//                existingProduct.setCategory(category);
//            }
//
//            // Map request to existing product
//            modelMapper.map(productRequest, existingProduct);
//            existingProduct.setUpdatedAt(LocalDateTime.now());
//
//            // Save updated product
//            Product updatedProduct = productRepository.save(existingProduct);
//            ProductDto response = modelMapper.map(updatedProduct, ProductDto.class);
//
//            return ApiResponse.success(response, "Product updated successfully", traceId, HttpStatus.OK);
//        } catch (ResourceNotFoundException ex) {
//            log.error("Error updating product: {}", ex.getMessage());
//            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
//        } catch (Exception ex) {
//            log.error("Error updating product: {}", ex.getMessage());
//            return ApiResponse.failure("Failed to update product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Override
//    @Transactional
//    public ApiResponse deleteProduct(Long productId) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            // Fetch product
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
//
//            // Delete product
//            productRepository.delete(product);
//
//            return ApiResponse.success(null, "Product deleted successfully", traceId, HttpStatus.OK);
//        } catch (ResourceNotFoundException ex) {
//            log.error("Error deleting product: {}", ex.getMessage());
//            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
//        } catch (Exception ex) {
//            log.error("Error deleting product: {}", ex.getMessage());
//            return ApiResponse.failure("Failed to delete product", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Override
//    public ApiResponse getAllProducts() {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            // Fetch all products
//            List<ProductDto> products = productRepository.findAll().stream()
//                    .map(product -> modelMapper.map(product, ProductDto.class))
//                    .collect(Collectors.toList());
//
//            return ApiResponse.success(products, "Products retrieved successfully", traceId, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("Error fetching all products: {}", ex.getMessage());
//            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
////
////    @Override
////    @Transactional
////    public ApiResponse getProductById(Long productId) {
////        String traceId = UUID.randomUUID().toString();
////        try {
////            log.info("[{}] Fetching product details for Product ID: {}", traceId, productId);
////
////            // Fetch product from the database
////            Product product = productRepository.findById(productId)
////                    .orElseThrow(() -> {
////                        log.error("[{}] Product not found for Product ID: {}", traceId, productId);
////                        return new ResourceNotFoundException("Product not found with ID: " + productId);
////                    });
////
////            // Fetch price and inventory in parallel
////            CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() ->
////                    fetchPriceFromPricingService(product, productId, traceId)
////            );
////            CompletableFuture<Void> inventoryFuture = CompletableFuture.runAsync(() ->
////                    fetchInventoryFromInventoryService(product, productId, traceId)
////            );
////
////            // Wait for both futures to complete
////            CompletableFuture.allOf(priceFuture, inventoryFuture).join();
////
////            // Save the updated product entity to the database
////            productRepository.save(product);
////
////            log.info("[{}] Product details retrieved successfully for Product ID: {}", traceId, productId);
////            return ApiResponse.success(product, "Product retrieved successfully with price and inventory", traceId, HttpStatus.OK);
////        } catch (ResourceNotFoundException ex) {
////            log.error("[{}] Product not found: {}", traceId, ex.getMessage());
////            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
////        } catch (Exception ex) {
////            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage());
////            return ApiResponse.failure("Failed to retrieve product details", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
////        }
////    }
//
//    @Override
//    public ApiResponse getProductById(Long productId) {
//        return null;
//    }
//
//    private Map<Long, Double> fetchPricesForProductIds(List<Long> productIds, String traceId) {
//        try {
//            log.info("[{}] Fetching prices for product IDs: {}", traceId, productIds);
//            ApiResponse<Map<Long, Double>> priceResponse = pricingServiceFeignClient.getPriceByProductId(productIds, "PRODUCT-SERVICE");
//
//            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
//                return priceResponse.getData();
//            } else {
//                log.warn("[{}] Prices not found for product IDs: {}", traceId, productIds);
//                return Collections.emptyMap();
//            }
//        } catch (Exception e) {
//            log.error("[{}] Error while fetching prices: {}", traceId, e.getMessage());
//            return Collections.emptyMap();
//        }
//    }
//    private Map<Long, Integer> fetchInventoriesForProductIds(List<Long> productIds, String traceId) {
//        try {
//            log.info("[{}] Fetching inventories for product IDs: {}", traceId, productIds);
//            ApiResponse<Map<Long, Integer>> inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productIds, "PRODUCT-SERVICE");
//
//            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
//                return inventoryResponse.getData();
//            } else {
//                log.warn("[{}] Inventories not found for product IDs: {}", traceId, productIds);
//                return Collections.emptyMap();
//            }
//        } catch (Exception e) {
//            log.error("[{}] Error while fetching inventories: {}", traceId, e.getMessage());
//            return Collections.emptyMap();
//        }
//    }
//
//    @Override
//    public ApiResponse getProductsByCategory(String category, String sortBy) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            log.info("[{}] Fetching products for category: {}", traceId, category);
//
//            // Fetch products by category
//            List<Product> products = productRepository.findAvailableProductsByCategoryName(category);
//            if (products.isEmpty()) {
//                return ApiResponse.failure("No products found for category: " + category, traceId, HttpStatus.NOT_FOUND);
//            }
//
//            // Extract product IDs
//            List<Long> productIds = products.stream()
//                    .map(Product::getId)
//                    .collect(Collectors.toList());
//
//            // Fetch prices and inventories in parallel
//            CompletableFuture<Map<Long, Double>> pricesFuture = CompletableFuture.supplyAsync(() ->
//                    fetchPricesForProductIds(productIds, traceId)
//            );
//            CompletableFuture<Map<Long, Integer>> inventoriesFuture = CompletableFuture.supplyAsync(() ->
//                    fetchInventoriesForProductIds(productIds, traceId)
//            );
//
//            // Wait for both futures to complete
//            CompletableFuture.allOf(pricesFuture, inventoriesFuture).join();
//
//            // Get results
//            Map<Long, Double> prices = pricesFuture.get();
//            Map<Long, Integer> inventories = inventoriesFuture.get();
//
//            // Map to DTOs
//            List<ProductDto> productDtos = products.stream()
//                    .map(product -> {
//                        ProductDto dto = modelMapper.map(product, ProductDto.class);
//                        dto.setPrice(prices.getOrDefault(product.getId(), Double.NaN)); // Use NaN if price is not available
//                        dto.setQuantity(inventories.getOrDefault(product.getId(), 0)); // Use 0 if inventory is not available
//                        return dto;
//                    })
//                    .filter(dto -> dto.getQuantity() > 0) // Filter out products with no inventory
//                    .sorted(getComparator(sortBy)) // Sort by price or inventory
//                    .collect(Collectors.toList());
//
//            if (productDtos.isEmpty()) {
//                return ApiResponse.failure("No products available with sufficient inventory for category: " + category, traceId, HttpStatus.OK);
//            }
//
//            return ApiResponse.success(productDtos, "Products retrieved successfully", traceId, HttpStatus.OK);
//        } catch (Exception ex) {
//            log.error("[{}] Error while fetching products for category: {}. Error: {}", traceId, category, ex.getMessage());
//            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    private Comparator<ProductDto> getComparator(String sortBy) {
//        if ("price".equalsIgnoreCase(sortBy)) {
//            return Comparator.comparing(ProductDto::getPrice);
//        } else if ("inventory".equalsIgnoreCase(sortBy)) {
//            return Comparator.comparing(ProductDto::getQuantity);
//        }
//        return Comparator.comparing(ProductDto::getId); // Default sorting by product ID
//    }
//}
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PricingServiceFeignClient pricingServiceFeignClient;

    @Autowired
    private InventoryServiceFeignClient inventoryServiceFeignClient;

    @Autowired
    private ObjectMapper objectMapper; // Autowired ObjectMapper bean

    @Override
    @Transactional
    public ApiResponse createProduct(ProductRequest productRequest) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("Creating product: {} | TraceId: {}", productRequest.getName(), traceId);

            // Check if product with the same name already exists
            if (productRepository.findByName(productRequest.getName()).isPresent()) {
                return ApiResponse.failure("Product with the same name already exists", traceId, HttpStatus.CONFLICT);
            }

            // Fetch category
            Category category = categoryRepository.findById(productRequest.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));

            // Map request to product entity
            Product product = modelMapper.map(productRequest, Product.class);
            product.setCategory(category);
            product.setQuantity(0); // Default quantity
            product.setPrice(0.0); // Default price

            // Save product
            Product savedProduct = productRepository.save(product);
            ProductDto response = modelMapper.map(savedProduct, ProductDto.class);

            return ApiResponse.success(response, "Product created successfully", traceId, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            log.error("Error creating product: {}", ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Error creating product: {}", ex.getMessage());
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
                Category category = categoryRepository.findById(productRequest.getCategory())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productRequest.getCategory()));
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
            log.error("Error updating product: {}", ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Error updating product: {}", ex.getMessage());
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
            log.error("Error deleting product: {}", ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Error deleting product: {}", ex.getMessage());
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
            log.error("Error fetching all products: {}", ex.getMessage());
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    @Transactional
    public ApiResponse getProductById(Long productId) {
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

            // Map Product to ProductDto using ModelMapper
            ProductDto productDto = modelMapper.map(product, ProductDto.class);

            // Check price and set to NaN if it is 0.0
            if (product.getPrice() == 0.0) {
                productDto.setPrice(Double.NaN);
            }

            if(product.getQuantity() == 0){
                productDto.setQuantityStatus("out of stock");
            }else {
                productDto.setQuantityStatus("In stock");
            }

            // Return the product details in the response
            return ApiResponse.success(productDto, "Product retrieved successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Product not found: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage());
            return ApiResponse.failure("Failed to retrieve product details", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private void fetchPriceFromPricingService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
            ApiResponse priceResponse = pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE");

            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
                PriceResponse price = objectMapper.convertValue(priceResponse.getData(), PriceResponse.class); // Use autowired ObjectMapper
                if (price != null) {
                    product.setPrice(price.getPrice());
                    log.info("[{}] Price successfully retrieved and attached to product: {}", traceId, price.getPrice());
                }
            } else {
                log.warn("[{}] Price not found for Product ID: {}. Proceeding without price.", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage());
        }
    }

    private void fetchInventoryFromInventoryService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
            ApiResponse inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE");

            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
                InventoryResponse inventory = objectMapper.convertValue(inventoryResponse.getData(), InventoryResponse.class); // Use autowired ObjectMapper
                if (inventory != null) {
                    product.setQuantity(inventory.getQuantity());
                    log.info("[{}] Inventory successfully retrieved: {} units for Product ID: {}", traceId, inventory.getQuantity(), productId);
                } else {
                    log.warn("[{}] Inventory data is null for Product ID: {}. Proceeding without inventory.", traceId, productId);
                }
            } else {
                log.warn("[{}] Inventory not found for Product ID: {}. Response: {}", traceId, productId, inventoryResponse);
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

            // Validate category name
            if (categoryName == null || categoryName.trim().isEmpty()) {
                log.error("[{}] Category name is null or empty.", traceId);
                return ApiResponse.failure("Category name must not be null or empty.", traceId, HttpStatus.BAD_REQUEST);
            }
            categoryName = categoryName.trim();

            // Fetch category
            String finalCategoryName = categoryName;
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> {
                        log.error("[{}] Category '{}' doesn't exist.", traceId, finalCategoryName);
                        return new ResourceNotFoundException("Category '" + finalCategoryName + "' doesn't exist.");
                    });

            // Validate sortBy parameter
            sortBy = (sortBy.equalsIgnoreCase("low") || sortBy.equalsIgnoreCase("high")) ? sortBy : "low";

            // Fetch products by category
            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName, sortBy);

            // Fetch price and inventory for each product in parallel
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    products.stream()
                            .map(product -> CompletableFuture.runAsync(() -> {
                                fetchPriceFromPricingService(product, product.getId(), traceId);
                                fetchInventoryFromInventoryService(product, product.getId(), traceId);
                            }))
                            .toArray(CompletableFuture[]::new)
            );
            allFutures.join(); // Wait for all futures to complete

            // Filter products with available inventory and map to DTOs
            List<ProductDto> productResponses = products.stream()
                    .filter(product -> product.getQuantity() > 0)
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .collect(Collectors.toList());

            if (productResponses.isEmpty()) {
                log.warn("[{}] No available products found for category: '{}'", traceId, categoryName);
                return ApiResponse.failure("No available products found for category '" + categoryName + "'.", traceId, HttpStatus.OK);
            }

            log.info("[{}] Products retrieved successfully for category: '{}'", traceId, categoryName);
            return ApiResponse.success(productResponses, "Products retrieved successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage());
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}