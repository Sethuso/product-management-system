package com.example.pricingservice.feignclient;

import com.example.pricingservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceFeignClient {

    @GetMapping("/com/api/product-service/product")
    ApiResponse getProductById(
            @RequestParam("id") Long productId,
            @RequestHeader("Service-Name") String serviceName
    );
}
