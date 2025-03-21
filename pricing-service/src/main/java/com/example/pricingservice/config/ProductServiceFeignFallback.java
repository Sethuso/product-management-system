package com.example.pricingservice.config;



import com.example.pricingservice.feignclient.ProductServiceFeignClient;
import com.example.pricingservice.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceFeignFallback implements ProductServiceFeignClient {

    @Override
    public ApiResponse getProductById(Long productId, String serviceName) {
        return ApiResponse.failure("Product Service is currently unavailable. Returning fallback response.",
                null,
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
