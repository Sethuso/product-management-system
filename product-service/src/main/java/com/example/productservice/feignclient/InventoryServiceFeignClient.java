package com.example.productservice.feignclient;

import com.example.productservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "http://localhost:8085/com/api")
public interface InventoryServiceFeignClient {

    @GetMapping("/getByProductId")
    ApiResponse getInventoryByProductId(@RequestParam("productId") Long productId);
}
