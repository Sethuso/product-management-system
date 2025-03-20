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
@RequestMapping("/com/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping
    public ApiResponse createProduct(@Valid @RequestBody ProductRequest product) {
        logger.info("Request to create product: {}", product.getName());
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ApiResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest product) {
        logger.info("Request to update product with ID: {}", id);
        return productService.updateProduct(id, product);
    }

    @GetMapping("/getByProductId")
    public ApiResponse getProductById(@RequestParam Long id) {
        logger.info("Request to retrieve product with ID: {}", id);
        return productService.getProductById(id);
    }

    @GetMapping
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
