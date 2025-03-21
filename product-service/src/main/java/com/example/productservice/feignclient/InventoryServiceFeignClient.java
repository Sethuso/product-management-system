package com.example.productservice.feignclient;

import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceFeignClient {

    @GetMapping("/com/api/inventory-service/getByProductId")
    ApiResponse<InventoryResponse> getInventoryByProductId(
            @RequestParam("productId") Long productId,
            @RequestHeader("Service-Name") String serviceName
    );
}
