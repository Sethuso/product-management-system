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
//
//    @Override
//    public ApiResponse getProductById(Long productId) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            log.info("[{}] Fetching product details for Product ID: {}", traceId, productId);
//
//            // Fetch product from the database
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> {
//                        log.error("[{}] Product not found for Product ID: {}", traceId, productId);
//                        return new ResourceNotFoundException("Product not found with ID: " + productId);
//                    });
//
//            log.info("[{}] Product details retrieved successfully for Product ID: {}", traceId, productId);
//
//            // Fetch price from Pricing Service
//            fetchPriceFromPricingService(product, productId, traceId);
//
//            // Fetch inventory from Inventory Service
//            fetchInventoryFromInventoryService(product, productId, traceId);
//
//            // Map Product to ProductDto using ModelMapper
//            ProductDto productDto = modelMapper.map(product, ProductDto.class);
//
//            // Check price and set to NaN if it is 0.0
//            if (product.getPrice() == 0.0) {
//                productDto.setPrice(Double.NaN);
//            }
//
//            // Set quantity status based on inventory
//            if (product.getQuantity() == 0) {
//                productDto.setQuantityStatus("out of stock");
//            } else {
//                productDto.setQuantityStatus("In stock");
//            }
//
//            // Return the product details in the response
//            return ApiResponse.success(productDto, "Product retrieved successfully", traceId, HttpStatus.OK);
//        } catch (ResourceNotFoundException ex) {
//            log.error("[{}] Product not found: {}", traceId, ex.getMessage());
//            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
//        } catch (Exception ex) {
//            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage());
//            return ApiResponse.failure("Failed to retrieve product details", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private void fetchPriceFromPricingService(Product product, Long productId, String traceId) {
//        try {
//            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
//
//            // Call the Pricing Service via Feign Client
//            ApiResponse<PriceResponse> priceResponse = pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE");
//            System.out.println(priceResponse);
//            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
//                PriceResponse price = priceResponse.getData();  // Directly get the PriceResponse object
//
//                if (price != null) {
//                    product.setPrice(price.getPrice());
//                    System.out.println(product);
//                    log.info("[{}] Price successfully retrieved and attached to product: {}", traceId, price.getPrice());
//                }
//            } else {
//                log.warn("[{}] Price not found for Product ID: {}. Proceeding without price.", traceId, productId);
//            }
//        } catch (Exception e) {
//            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
//        }
//    }
//
//
//    private void fetchInventoryFromInventoryService(Product product, Long productId, String traceId) {
//        try {
//            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
//
//            // Call the Inventory Service via Feign Client
//            ApiResponse<InventoryResponse> inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE");
//
//            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
//                InventoryResponse inventory = inventoryResponse.getData();  // Directly get InventoryResponse
//
//                if (inventory != null) {
//                    product.setQuantity(inventory.getQuantity());
//                    log.info("[{}] Inventory successfully retrieved: {} units for Product ID: {}", traceId, inventory.getQuantity(), productId);
//                }
//            } else {
//                log.warn("[{}] Inventory not found for Product ID: {}. Response: {}", traceId, productId, inventoryResponse);
//            }
//        } catch (Exception e) {
//            log.error("[{}] Error while fetching inventory for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
//        }
//    }
//    @Override
//    @Transactional
//    public ApiResponse findAvailableProductsByCategory(String categoryName, String sortBy) {
//        String traceId = UUID.randomUUID().toString();
//        try {
//            log.info("[{}] Fetching available products for category: '{}'", traceId, categoryName);
//
//            // Validate category name
//            if (categoryName == null || categoryName.trim().isEmpty()) {
//                log.error("[{}] Category name is null or empty.", traceId);
//                return ApiResponse.failure("Category name must not be null or empty.", traceId, HttpStatus.BAD_REQUEST);
//            }
//
//            categoryName = categoryName.trim();
//
//            // Fetch category
//            String finalCategoryName = categoryName;
//            Category category = categoryRepository.findByName(categoryName)
//                    .orElseThrow(() -> {
//                        log.error("[{}] Category '{}' doesn't exist.", traceId, finalCategoryName);
//                        return new ResourceNotFoundException("Category '" + finalCategoryName + "' doesn't exist.");
//                    });
//
//            // Handle sortBy parameter (defaulting to "low" if invalid)
//            if (!"low".equalsIgnoreCase(sortBy) && !"high".equalsIgnoreCase(sortBy)) {
//                log.warn("[{}] Invalid sortBy parameter '{}'. Defaulting to 'low'.", traceId, sortBy);
//                sortBy = "low";
//            }
//
//            // Fetch products by category
//            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName, sortBy);
//
//            List<ProductDto> productResponses = products.stream()
//                    .map(product -> {
//                        Long productId = product.getId();
//
//                        // Fetch price from Pricing Service
//                        try {
//                            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
//                            ApiResponse<PriceResponse> priceResponse = pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE");
//
//                            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
//                                PriceResponse price = priceResponse.getData();
//                                if (price != null) {
//                                    product.setPrice(price.getPrice());
//                                    log.info("[{}] Price successfully retrieved for Product ID: {}", traceId, productId);
//                                }
//                            }
//                        } catch (Exception e) {
//                            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
//                        }
//
//                        // Fetch inventory from Inventory Service
//                        try {
//                            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
//                            ApiResponse<InventoryResponse> inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE");
//
//                            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
//                                InventoryResponse inventory = inventoryResponse.getData();
//                                if (inventory != null) {
//                                    product.setQuantity(inventory.getQuantity());
//                                    log.info("[{}] Inventory successfully retrieved for Product ID: {}", traceId, productId);
//                                }
//                            }
//                        } catch (Exception e) {
//                            log.error("[{}] Error while fetching inventory for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
//                        }
//
//                        // Map product to ProductDto
//                        ProductDto productDto = modelMapper.map(product, ProductDto.class);
//
//                        // Set price to NaN if it's 0.0 (invalid price)
//                        if (product.getPrice() == 0.0) {
//                            productDto.setPrice(Double.NaN);
//                        }
//
//                        // Set quantity status
//                        productDto.setQuantityStatus(product.getQuantity() > 0 ? "in stock" : "out of stock");
//
//                        return productDto;
//                    })
//                    .collect(Collectors.toList());
//
//            if (productResponses.isEmpty()) {
//                log.warn("[{}] No products found for category: '{}'", traceId, categoryName);
//                return ApiResponse.failure("No products found for category '" + categoryName + "'.", traceId, HttpStatus.OK);
//            }
//
//            log.info("[{}] Products retrieved successfully for category: '{}'", traceId, categoryName);
//            return ApiResponse.success(productResponses, "Products retrieved successfully", traceId, HttpStatus.OK);
//
//        } catch (ResourceNotFoundException ex) {
//            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage());
//            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
//        } catch (Exception ex) {
//            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage(), ex);
//            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//}

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

            // Fetch price and inventory in parallel
            CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> fetchPriceFromPricingService(product, productId, traceId));
            CompletableFuture<Void> inventoryFuture = CompletableFuture.runAsync(() -> fetchInventoryFromInventoryService(product, productId, traceId));

            // Wait for both futures to complete
            CompletableFuture.allOf(priceFuture, inventoryFuture).join();

            // Map Product to ProductDto
            ProductDto productDto = modelMapper.map(product, ProductDto.class);

            // Set price to NaN if it's 0.0
            if (product.getPrice() == 0.0) {
                productDto.setPrice(Double.NaN);
            }

            // Set quantity status
            productDto.setQuantityStatus(product.getQuantity() > 0 ? "in stock" : "out of stock");

            return ApiResponse.success(productDto, "Product retrieved successfully", traceId, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Product not found: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching product details: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to retrieve product details", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void fetchPriceFromPricingService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching price from Pricing Service for Product ID: {}", traceId, productId);
            ApiResponse<PriceResponse> priceResponse = pricingServiceFeignClient.getPriceByProductId(productId, "PRODUCT-SERVICE");

            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getData() != null) {
                PriceResponse price = priceResponse.getData();
                if (price != null) {
                    product.setPrice(price.getPrice());
                    log.info("[{}] Price successfully retrieved for Product ID: {}", traceId, productId);
                }
            } else {
                log.warn("[{}] Price not found for Product ID: {}", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching price for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
        }
    }

    private void fetchInventoryFromInventoryService(Product product, Long productId, String traceId) {
        try {
            log.info("[{}] Fetching inventory from Inventory Service for Product ID: {}", traceId, productId);
            ApiResponse<InventoryResponse> inventoryResponse = inventoryServiceFeignClient.getInventoryByProductId(productId, "PRODUCT-SERVICE");

            if (inventoryResponse != null && inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
                InventoryResponse inventory = inventoryResponse.getData();
                if (inventory != null) {
                    product.setQuantity(inventory.getQuantity());
                    log.info("[{}] Inventory successfully retrieved for Product ID: {}", traceId, productId);
                }
            } else {
                log.warn("[{}] Inventory not found for Product ID: {}", traceId, productId);
            }
        } catch (Exception e) {
            log.error("[{}] Error while fetching inventory for Product ID: {}. Error: {}", traceId, productId, e.getMessage(), e);
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

            // Handle sortBy parameter (defaulting to "low" if invalid)
            if (!"low".equalsIgnoreCase(sortBy) && !"high".equalsIgnoreCase(sortBy)) {
                log.warn("[{}] Invalid sortBy parameter '{}'. Defaulting to 'low'.", traceId, sortBy);
                sortBy = "low";
            }

            // Fetch products by category
            List<Product> products = productRepository.findAvailableProductsByCategoryName(categoryName, sortBy);

            // Fetch price and inventory for each product in parallel
            List<CompletableFuture<Void>> futures = products.stream()
                    .map(product -> CompletableFuture.runAsync(() -> {
                        fetchPriceFromPricingService(product, product.getId(), traceId);
                        fetchInventoryFromInventoryService(product, product.getId(), traceId);
                    }))
                    .collect(Collectors.toList());

            // Wait for all futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Map products to ProductDto
            List<ProductDto> productResponses = products.stream()
                    .map(product -> {
                        ProductDto productDto = modelMapper.map(product, ProductDto.class);

                        // Set price to NaN if it's 0.0
                        if (product.getPrice() == 0.0) {
                            productDto.setPrice(Double.NaN);
                        }

                        // Set quantity status
                        productDto.setQuantityStatus(product.getQuantity() > 0 ? "in stock" : "out of stock");

                        return productDto;
                    })
                    .collect(Collectors.toList());

            if (productResponses.isEmpty()) {
                log.warn("[{}] No products found for category: '{}'", traceId, categoryName);
                return ApiResponse.failure("No products found for category '" + categoryName + "'.", traceId, HttpStatus.OK);
            }

            log.info("[{}] Products retrieved successfully for category: '{}'", traceId, categoryName);
            return ApiResponse.success(productResponses, "Products retrieved successfully", traceId, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage());
            return ApiResponse.failure(ex.getMessage(), traceId, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("[{}] Error while fetching products: {}", traceId, ex.getMessage(), ex);
            return ApiResponse.failure("Failed to retrieve products", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}