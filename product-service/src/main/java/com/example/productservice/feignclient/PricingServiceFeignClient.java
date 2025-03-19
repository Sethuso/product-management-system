package com.example.productservice.feignclient;


import com.example.productservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pricing-service", url = "http://localhost:8084/com/api")  // Update URL as needed
public interface PricingServiceFeignClient {

    @GetMapping("/getProductId")
    ApiResponse getPriceByProductId(@RequestParam("productId") Long productId);
}