package com.example.productservice.config;

import com.example.productservice.feignclient.InventoryServiceFeignClient;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.InventoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFeignFallback implements InventoryServiceFeignClient {

    @Override
    public ApiResponse getInventoryByProductId(Long productId, String serviceName) {
        return ApiResponse.failure("Inventory Service is currently unavailable. Returning fallback response.",
                null,
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
