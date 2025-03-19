package com.example.pricingservice.feignclient;

import com.example.pricingservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/products")
public interface ProductServiceFeignClient {

    @GetMapping("/getByProductId")
    ApiResponse getProductById(@RequestParam("id") Long productId);
}
