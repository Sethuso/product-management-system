package com.example.productservice.service;

import com.example.productservice.model.Product;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ApiResponse;

public interface ProductService {

    ApiResponse createProduct(ProductRequest product);

    ApiResponse updateProduct(Long productId, ProductRequest product);

    ApiResponse deleteProduct(Long productId);

    ApiResponse getProductById(Long productId);

    ApiResponse getAllProducts();
    ApiResponse findAvailableProductsByCategory(String categoryName, String sortBy);
}
