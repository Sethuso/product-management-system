package com.example.productservice.config;

import com.example.productservice.feignclient.PricingServiceFeignClient;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.PriceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PricingServiceFeignFallback implements PricingServiceFeignClient {
    @Override
    public ApiResponse<PriceResponse> getPriceByProductId(Long productId, String serviceName) {
        return ApiResponse.failure(
                "Pricing Service is currently unavailable. Returning fallback response.",
                null,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}
