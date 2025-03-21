package com.example.productservice.controller;

import com.example.productservice.request.ProductRequest;
import com.example.productservice.request.CategoryRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.service.ProductService;
import com.example.productservice.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/com/api/product-service")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public ApiResponse createProduct(@Valid @RequestBody ProductRequest product) {
        logger.info("Request to create product: {}", product.getName());
        return productService.createProduct(product);
    }

    @PutMapping("/product")
    public ApiResponse updateProduct(@RequestParam Long id, @RequestBody ProductRequest product) {
        logger.info("Request to update product with ID: {}", id);
        return productService.updateProduct(id, product);
    }

    @GetMapping("/product")
    public ApiResponse getProductById(@RequestParam("id") Long productId,
                                      @RequestHeader(value = "Service-Name", required = false) String serviceName) {

        if (serviceName != null) {
            logger.info("Request received from Service: {}", serviceName);
        } else {
            logger.info("Request received from an external client.");
        }

        return productService.getProductById(productId);
    }

    @GetMapping("/products")
    public ApiResponse getAllProducts() {
        logger.info("Request to retrieve all products");
        return productService.getAllProducts();
    }
    @GetMapping("/get_products_by_category")
    public ApiResponse findAvailableProductsByCategory(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "low") String priceRange) {
        categoryName = categoryName.trim();
        logger.info("Request to find available products by category: {}", categoryName);
        return productService.findAvailableProductsByCategory(categoryName, priceRange);
    }

}
