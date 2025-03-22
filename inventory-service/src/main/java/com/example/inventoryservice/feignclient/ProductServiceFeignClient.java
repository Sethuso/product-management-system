package com.example.inventoryservice.feignclient;

import com.example.inventoryservice.config.ProductServiceFeignFallback;
import com.example.inventoryservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "PRODUCT-SERVICE",fallback = ProductServiceFeignFallback.class)
public interface ProductServiceFeignClient {

    @GetMapping("/com/api/product-service/product")
    ApiResponse getProductById(
            @RequestParam("id") Long productId,
            @RequestHeader("Service-Name") String serviceName
    );
}
